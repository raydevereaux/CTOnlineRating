package com.bc.ct.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bc.ct.beans.Commodity;
import com.bc.ct.repository.CommodityRepository;
import com.google.common.base.Optional;

@Service
public class CommodityServiceImpl implements CommodityService {

	@Autowired
	private CommodityRepository repo;
	
	@Override
	public List<Integer> getAllCommodityCodes() {
		return repo.getAllCommodityCodes();
	}

	@Cacheable("commodity")
	@Override
	public List<Commodity> getCommodityList(Optional<String> client) {
		if (client.isPresent()) {
			try {
				return repo.getCommodityList(client.get());	
			}catch(Exception sqlException) {
				return repo.getCommodityList();
			}
		}else {
			return repo.getCommodityList();
		}
	}
}
