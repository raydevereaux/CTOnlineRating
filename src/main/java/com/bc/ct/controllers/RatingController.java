package com.bc.ct.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.repository.GeographyRepository;
import com.bc.ct.ws.model.RateRequest;

@Controller
public class RatingController {

	@Autowired
	private GeographyRepository repo;
	
	@RequestMapping("/")
	private String index() {
		return "rating";
	}
	
	@RequestMapping(value = "/refreshODPairs", method = RequestMethod.GET)
	@ResponseBody
	private void refreshODPairs() {
		//TODO: Fill out method
		System.out.println("OD Pairs Refreshed");
		repo.testRead();
	}
	
	@RequestMapping(value = "/rate", method = RequestMethod.POST)
	private void rate(@RequestParam RateRequest rateRequest) {
		//TODO: Fill out method
		System.out.println("Getting rate from CT");
	}
	
	@RequestMapping(value = "/getShippers.json", method = RequestMethod.GET)
	@ResponseBody
	private void getShippersJSON() {
		//TODO: Fill out method
		System.out.println("Returning a list of shippers in JSON form");
	}
	
}
