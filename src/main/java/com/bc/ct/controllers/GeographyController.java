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
	
	@RequestMapping(value = "/allLocations.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Location> getAllLocations() {
		return geoService.getAllLocations();
	}
	
	@RequestMapping(value = "/locationByCode.json", method = RequestMethod.GET)
	@ResponseBody
	private Location getLocationByCode(@RequestParam String locationCode) {
		return geoService.getLocation(locationCode);
	}
	
	@RequestMapping(value = "/allSpellCheckLocations.json", method = RequestMethod.GET)
	@ResponseBody
	private List<Location> getAllSpellCheckLocations() {
		return geoService.getSpellCheckLocations("DENVER", "CO", Optional.<String>absent());
	}
}
