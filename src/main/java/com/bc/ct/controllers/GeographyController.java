package com.bc.ct.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.beans.Location;
import com.bc.ct.service.GeographyService;
import com.google.common.base.Optional;

@Controller
public class GeographyController {

	@Autowired
	private GeographyService geoService;
	
	@RequestMapping(value = "/allMillLocations.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Location> getAllMillLocations() {
		return geoService.getAllMillLocations();
	}
	
	@RequestMapping(value = "/millLocationByCode.json", method = RequestMethod.GET)
	@ResponseBody
	private Location getMillLocationByCode(@RequestParam(required=false) String client, @RequestParam String locationCode) {
		return geoService.getMillLocation(Optional.fromNullable(client), locationCode);
	}
	
	@RequestMapping(value = "/spellCheckLocations.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Location> getSpellCheckLocations(@RequestParam String city, @RequestParam String state,
			@RequestParam(required=false) String zip) {
		return geoService.getSpellCheckLocations(city, state, Optional.fromNullable(zip));
	}
}
