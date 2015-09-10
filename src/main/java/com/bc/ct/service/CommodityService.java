package com.bc.ct.service;

import java.util.List;

import com.bc.ct.beans.Commodity;
import com.google.common.base.Optional;

public interface CommodityService {

	public List<Integer> getAllCommodityCodes();
	public List<Commodity> getCommodityList(Optional<String> client);
}
