package de.zalando.zmon.dataservice.proxies.scheduler;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import de.zalando.zmon.dataservice.DataServiceMetrics;
import de.zalando.zmon.dataservice.config.DataServiceConfigProperties;
import de.zalando.zmon.dataservice.config.ObjectMapperConfig;

@ContextConfiguration
public class DataCenterControllerTest extends AbstractDataCenterControllerTest {

    @Configuration
    @Import({ SchedulerConfig.class, ObjectMapperConfig.class })
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
