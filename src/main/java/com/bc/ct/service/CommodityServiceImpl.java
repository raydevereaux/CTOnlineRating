package com.bc.ct.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.ct.beans.Commodity;
import com.bc.ct.repository.CommodityRepository;

@Service
public class CommodityServiceImpl implements CommodityService {

	@Autowired
	private CommodityRepository repo;
	
	@Override
	public List<Integer> getAllCommodityCodes() {
		return repo.getAllCommodityCodes();
	}

	@Override
	public List<Commodity> getCommodityList() {
		return repo.getCommodityList();
	}

	@Override
	public List<Commodity> getCommodityList(String client) {
		return repo.getCommodityList(client);
	}

}
