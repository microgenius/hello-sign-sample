package com.tanriverdi.hellosignsample;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.*;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@SpringBootApplication
@EnableCaching
public class HelloSignSampleApplication {
    private static final long MAX_TIME_TO_LIVE_DURATION_IN_MINUTES = 5;

    public static void main(String[] args) {
        SpringApplication.run(HelloSignSampleApplication.class, args);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(MAX_TIME_TO_LIVE_DURATION_IN_MINUTES))
            .disableCachingNullValues()
            .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
