package com.bc.ct.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.beans.Location;
import com.bc.ct.repository.GeographyRepository;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateRequest.Commoditys;
import com.bc.ct.ws.model.RateRequest.Commoditys.Commodity;
import com.bc.ct.ws.model.RateRequest.Stops;
import com.bc.ct.ws.model.RateRequest.Stops.Stop;

@Controller
public class RatingController {

	@Autowired
	private GeographyRepository repo;
	@Autowired
	public CacheManager cacheManager;
	
	@RequestMapping("/")
	private String index(Model model) {
		RateRequest rateRequest = new RateRequest();
		Commoditys comms = new Commoditys();
		Commodity comm = new Commodity();
		comms.setCommodity(comm);
		rateRequest.getCommoditys().add(comms);
		Stops stops = new Stops();
		Stop stop = new Stop();
		stops.setStop(stop);
		rateRequest.getStops().add(stops);
		model.addAttribute("rateRequest", rateRequest);
		return "rating";
	}
	
	@RequestMapping(value = "/refreshODPairs", method = RequestMethod.GET)
	@ResponseBody
	private Location refreshODPairs() {
		System.out.println("OD Pairs Refreshed");
		Location location = repo.getLocation("31");
		return location;
	}
	
	@RequestMapping(value = "/rate", method = RequestMethod.POST)
	@ResponseBody
	private RateRequest rate(@ModelAttribute RateRequest rateRequest) {
		return rateRequest;
	}
	
	@RequestMapping(value = "/clearAllCaches")
	@CacheEvict(value = {"commodity", "carrier", "geography"}, allEntries=true)
	@ResponseBody
	private String clearAllCaches() {
		return "Cleared all caches from the CT Online Rating.";
	}
}
