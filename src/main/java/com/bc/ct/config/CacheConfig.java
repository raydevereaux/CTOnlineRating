package com.bc.ct.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
	 SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
	 GuavaCache cache1 = new GuavaCache("commodity", CacheBuilder.newBuilder()
             .expireAfterAccess(120, TimeUnit.MINUTES)
             .build());
	 GuavaCache cache2 = new GuavaCache("carrier", CacheBuilder.newBuilder()
	             .expireAfterAccess(120, TimeUnit.MINUTES)
	             .build());
	 GuavaCache cache3 = new GuavaCache("geography", CacheBuilder.newBuilder()
             .expireAfterAccess(120, TimeUnit.MINUTES)
             .build());
	 simpleCacheManager.setCaches(Arrays.asList(cache1, cache2, cache3));
	 return simpleCacheManager;
	}
}
