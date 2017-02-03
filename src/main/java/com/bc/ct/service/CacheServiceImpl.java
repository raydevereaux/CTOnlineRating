package com.bc.ct.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

	@Autowired
	private CacheManager cacheManager;

	/* (non-Javadoc)
	 * @see com.bc.ct.service.CacheService#clearAllCaches()
	 */
	@Override
	public void clearAllCaches() {
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}
}
