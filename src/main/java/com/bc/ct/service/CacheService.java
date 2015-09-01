package com.bc.ct.service;

import org.springframework.cache.annotation.CacheEvict;

public interface CacheService {

	void clearAllCaches();

}