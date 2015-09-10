package com.bc.ct.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.beans.Commodity;
import com.bc.ct.service.CommodityService;
import com.google.common.base.Optional;

@Controller
public class CommodityController {

	@Autowired
	private CommodityService commService;
	
	@RequestMapping(value = "/allCommodityCodes.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Integer> getAllCommodityCodes() {
		return commService.getAllCommodityCodes();
	}
	
	@RequestMapping(value = "/commodityList.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Commodity> getCommodityList(@RequestParam(required=false) String client) {
		return commService.getCommodityList(Optional.<String>fromNullable(client));	
	}
}
