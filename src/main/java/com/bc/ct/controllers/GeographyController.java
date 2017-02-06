package com.bc.ct.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.beans.Location;
import com.bc.ct.service.GeographyService;
import com.google.common.base.Optional;

@Controller
public class GeographyController {

	@Autowired
	private GeographyService geoService;
	
	@GetMapping("/allMillLocations.json")
	@ResponseBody
	private List<Location> getAllMillLocations(@RequestParam(required=false) String client) {
		return geoService.getAllMillLocations(Optional.fromNullable(client));
	}
	
	@GetMapping("/millLocationByCode.json")
	@ResponseBody
	private Location getMillLocationByCode(@RequestParam(required=false) String client, @RequestParam String locationCode) {
		return geoService.getMillLocation(Optional.fromNullable(client), locationCode);
	}
	
	@GetMapping("/spellCheckLocations.json")
	@ResponseBody
	private List<Location> getSpellCheckLocations(@RequestParam String city, @RequestParam String state,
			@RequestParam(required=false) String zip) {
		return geoService.getSpellCheckLocations(city, state, Optional.fromNullable(zip));
	}
}
