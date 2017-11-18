package de.zalando.zmon.dataservice.data;

import de.zalando.zmon.dataservice.config.DataServiceConfigProperties;
import de.zalando.zmon.dataservice.config.RedisDataPointsStoreProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class QueryStoreConfig {

    @Bean("redisDataPointsJedisPool")
    @ConditionalOnProperty(name = "dataservice.data_points_store_properties.enabled", havingValue = "true")
    JedisPool redisDataPointsJedisPool(final DataServiceConfigProperties config) {
        final RedisDataPointsStoreProperties storeProperties = config.getDataPointsStoreProperties();
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setMaxTotal(storeProperties.getPoolSize());
        return new JedisPool(poolConfig, storeProperties.getHost(), storeProperties.getPort(), storeProperties.getTimeOut());
    }

    @Bean
    @ConditionalOnBean(name = "redisDataPointsJedisPool")
    DataPointsQueryStore redisDataPointsQueryStore(final JedisPool jedisPool) {
        return new RedisDataPointsQueryStore(jedisPool);
    }

    @Bean
    @ConditionalOnMissingBean
    DataPointsQueryStore kairosDataPointsQueryStore(DataServiceConfigProperties config) {
        return new KairosDataPointsQueryStore(config);
    }
}
