package de.zalando.zmon.dataservice.data;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import de.zalando.zmon.dataservice.ApplianceVersionService;
import de.zalando.zmon.dataservice.oauth2.BearerToken;
import de.zalando.zmon.dataservice.proxies.entities.EntitiesService;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.zalando.zmon.dataservice.AbstractControllerTest;
import de.zalando.zmon.dataservice.DataServiceMetrics;
import de.zalando.zmon.dataservice.components.CustomObjectMapper;
import de.zalando.zmon.dataservice.components.DefaultObjectMapper;
import de.zalando.zmon.dataservice.config.DataServiceConfigProperties;
import de.zalando.zmon.dataservice.config.ObjectMapperConfig;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration
public class DataServiceControllerTest extends AbstractControllerTest {

    @Autowired
    private DataServiceMetrics metrics;

    private RedisDataStore storage;

    private KairosDBStore kairosStore;

    private ProxyWriter proxyWriter;

    @Autowired
    @DefaultObjectMapper
    private ObjectMapper defaultObjectMapper;

    @Autowired
    @CustomObjectMapper
    private ObjectMapper customObjectMapper;

    DataServiceController controller;

    List<WorkResultWriter> workResultWriter;

    ApplianceVersionService applianceVersionService;

    DataServiceConfigProperties config = new DataServiceConfigProperties();

    EntitiesService entitiesService;

    @Before
    public void setUp() {

        Timer timer = Mockito.mock(Timer.class);
        Context context = Mockito.mock(Context.class);
        when(timer.time()).thenReturn(context);
        when(metrics.getKairosDBTimer()).thenReturn(timer);

        storage = Mockito.mock(RedisDataStore.class);
        kairosStore = Mockito.mock(KairosDBStore.class);
        proxyWriter = Mockito.mock(ProxyWriter.class);
        entitiesService = Mockito.mock(EntitiesService.class);
        applianceVersionService = new ApplianceVersionService(entitiesService);

        controller = new DataServiceController(storage, metrics, defaultObjectMapper, customObjectMapper,
                workResultWriter, proxyWriter, config, applianceVersionService);
    }

    @After
    public void cleanMocks() {
        Mockito.reset(storage, kairosStore, metrics);
    }

    @Test
    public void extract() {
        Optional<WorkerResult> wrOptional = controller.extractAndFilter("{}", "stups", 13);
        Assertions.assertThat(wrOptional.get()).isNotNull();
        Assertions.assertThat(wrOptional.get().results).isEmpty();
    }

    @Test
    public void extractWithException() {
        // we use null to fail
        Optional<WorkerResult> wrOptional = controller.extractAndFilter(null, "stups", 13);
        Assertions.assertThat(wrOptional.isPresent()).isFalse();
        Mockito.verify(metrics, Mockito.atLeast(1)).markParseError();
    }

    @Test
    public void tialRunWithException() {
        DataServiceController controllerSpy = Mockito.spy(controller);

        controllerSpy.putTrialRunData("");

        Mockito.verify(metrics, Mockito.atLeastOnce()).markTrialRunData();
        Mockito.verify(metrics, Mockito.atLeastOnce()).markTrialRunError();
    }

    @Test
    public void tialRun() throws IOException {
        DataServiceController controllerSpy = Mockito.spy(controller);

        controllerSpy.putTrialRunData(resourceToString(jsonResource("trialRun")));

        Mockito.verify(metrics, Mockito.atLeastOnce()).markTrialRunData();
        Mockito.verify(metrics, Mockito.never()).markTrialRunError();
    }

    @Test
    public void extractToken() {
        Optional<String> token = BearerToken.extractFromHeader("Bearer 123456789");
        Assertions.assertThat(token).isNotNull();
        Assertions.assertThat(token.isPresent()).isTrue();
        Assertions.assertThat(token.get()).isEqualTo("123456789");
    }

    @Test
    public void writeToProxy() {
        controller.proxyData("Bearer 123456789", "123", "12345", "");
        Mockito.verify(proxyWriter, Mockito.times(1)).write("123456789", "123", "12345", "");
    }

    @Test
    public void testConfigStuff() throws Exception {
        when(entitiesService.getEntities(eq(Optional.of("12345")), eq("[{\"id\":\"zmon-appliance-config\"}]"), eq(""))).thenReturn("[{\"data\":{\"version-config\":{}}}]");
        ResponseEntity<JsonNode> node = controller.getVersionConfig("Bearer 12345");
        Mockito.verify(entitiesService).getEntities(eq(Optional.of("12345")), eq("[{\"id\":\"zmon-appliance-config\"}]"), eq(""));
        Assertions.assertThat(node.getBody()).isNotNull();
    }

    @Configuration
    @Import({ ObjectMapperConfig.class })
    static class TestConfig {

        @Bean
        public DataServiceConfigProperties dataServiceConfigProperties() {
            return new DataServiceConfigProperties();
        }

        @Bean
        public DataServiceMetrics dataServiceMetrics() {
            return Mockito.mock(DataServiceMetrics.class);
        }
    }
}
