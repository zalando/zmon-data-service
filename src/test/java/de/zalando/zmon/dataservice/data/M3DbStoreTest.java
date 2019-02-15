package de.zalando.zmon.dataservice.data;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import de.zalando.zmon.dataservice.AbstractControllerTest;
import de.zalando.zmon.dataservice.DataServiceMetrics;
import de.zalando.zmon.dataservice.config.DataServiceConfigProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/** @author raparida on 15.02.19 */
@ContextConfiguration
public class M3DbStoreTest extends AbstractControllerTest {

  @Rule public final WireMockRule wireMockRule = new WireMockRule(10081);

  @Autowired private DataPointsQueryStore dataPointsQueryStore;

  @Autowired private DataServiceConfigProperties config;

  @Autowired private DataServiceMetrics metrics;

  @Before
  public void setUp() {
    wireMockRule.stubFor(
        post(urlPathEqualTo("/api/v1/datapoints"))
            .willReturn(aResponse().withStatus(200).withBody("{}").withFixedDelay(200)));
  }

  @Test
  public void writeWorkerResult() {
    M3DbStore m3Db = new M3DbStore(config, metrics, dataPointsQueryStore);
    m3Db.store(Fixture.buildGenericMetrics());
    verify(dataPointsQueryStore, atMost(1)).store(anyString());
    verify(metrics, never()).markM3DbError();
    verify(metrics, never()).markM3DbHostErrors(anyLong());
  }

  @Test
  public void testInvalidWorkerResult() {
    M3DbStore m3Db = new M3DbStore(config, metrics, dataPointsQueryStore);
    for (List<GenericMetrics> metricsList :
        new ArrayList<List<GenericMetrics>>(Arrays.asList(null, new ArrayList<GenericMetrics>()))) {
      m3Db.store(metricsList);
      verify(metrics, never()).incKairosDBDataPoints(anyLong());
      verify(dataPointsQueryStore, never()).store(anyString());
    }
  }

  @Configuration
  static class TestConfig {

    @Bean
    public DataServiceConfigProperties dataServiceConfigProperties() {
      DataServiceConfigProperties props = new DataServiceConfigProperties();
      props.setM3DbWriteUrls(ImmutableList.of(ImmutableList.of("http://localhost:10081")));
      props.setLogM3dbRequests(true);
      props.setLogM3dbErrors(true);
      return props;
    }

    @Bean
    public DataServiceMetrics dataServiceMetrics() {
      return mock(DataServiceMetrics.class);
    }

    @Bean
    public DataPointsQueryStore dataPointsStore() {
      return mock(DataPointsQueryStore.class);
    }
  }
}