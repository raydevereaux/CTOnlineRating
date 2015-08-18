package com.bc.ct.repository;

import java.util.List;

import com.bc.ct.beans.Commodity;

public interface CommodityRepository {

	public List<Integer> getAllCommodityCodes();
	public List<Commodity> getCommodityList();
	public List<Commodity> getCommodityList(String client);
}
