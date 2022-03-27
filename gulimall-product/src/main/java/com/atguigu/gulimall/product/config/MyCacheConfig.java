package com.atguigu.gulimall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author Dali
 * @Date 2022/3/19 9:37
 * @Version 1.0
 * @Description: 自己配置redisCacheConfiguration
 * @Configuration
 * @EnableCaching：开启缓存注解
 * @EnableConfigurationProperties ： 开启属性的绑定功能
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

	// 方式一
	@Autowired
	CacheProperties cacheProperties;

	@Bean
	RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) { // 方式二

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//		config = config.entryTtl()
		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

		CacheProperties.Redis redisProperties = cacheProperties.getRedis();
		// 将配置文件中的所有配置都生效
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		return config;
	}
}
