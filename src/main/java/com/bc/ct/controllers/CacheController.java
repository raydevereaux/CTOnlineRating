package com.bc.ct.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.service.CacheService;

@Controller
public class CacheController {

	@Autowired
	private CacheService cacheService;
	
	@RequestMapping(value = "/clearAllCaches")
	@ResponseBody
	private String clearAllCaches() {
		cacheService.clearAllCaches();
		return "Cleared all caches from the CT Online Rating.";
	}
}
