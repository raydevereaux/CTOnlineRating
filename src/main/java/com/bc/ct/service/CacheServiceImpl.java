package com.bc.ct.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {


	/* (non-Javadoc)
	 * @see com.bc.ct.service.CacheService#clearAllCaches()
	 */
	@Override
	@CacheEvict(value = {"commodity", "carrier", "geography"}, allEntries=true)
	public void clearAllCaches() {
	}
}
