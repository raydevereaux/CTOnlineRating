package com.bc.ct.service;

import java.util.List;

import com.bc.ct.beans.Commodity;

public interface CommodityService {

	public List<Integer> getAllCommodityCodes();
	public List<Commodity> getCommodityList();
	public List<Commodity> getCommodityList(String client);
}
