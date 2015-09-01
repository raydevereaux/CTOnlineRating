package com.bc.ct.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.beans.Location;
import com.bc.ct.excel.RateQuoteExcel;
import com.bc.ct.repository.GeographyRepository;
import com.bc.ct.service.CacheService;
import com.bc.ct.service.RatingService;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateRequest.Commoditys;
import com.bc.ct.ws.model.RateRequest.Commoditys.Commodity;
import com.bc.ct.ws.model.RateRequest.Stops;
import com.bc.ct.ws.model.RateRequest.Stops.Stop;
import com.bc.ct.ws.model.RateResponse;
import com.google.common.base.Optional;

@Controller
public class RatingController {

	@Autowired
	private GeographyRepository repo;
	@Autowired
	private RatingService rateService;
	@Autowired
	private CacheService cacheService;
	
	private Logger logger = LoggerFactory.getLogger(RatingController.class);
	
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
		Location location = repo.getMillLocation(Optional.<String>of("BOISEW"), "31");
		return location;
	}
	
	@RequestMapping(value = "/rate", method = RequestMethod.POST)
	private String rate(@ModelAttribute RateRequest rateRequest, Model model) {
		model.addAttribute("rateResponse", rateService.rate(rateRequest));
		return "ratingQuotes :: ratingQuoteTable";
	}

	@RequestMapping(value = "/rateExcel", method = RequestMethod.GET)
	private void rateExcel(@ModelAttribute RateRequest rateRequest, HttpServletResponse response) {
		RateResponse rateResponse = rateService.rate(rateRequest);
		RateQuoteExcel excel = new RateQuoteExcel(rateRequest, rateResponse);
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + excel.getFileName() + ".xlsx\"");
			excel.write(response.getOutputStream());	
		}catch(IOException ex) {
			logger.info("Error while writing excel to output stream.", ex);
			throw new RuntimeException("IOError while writing dealer sales diff report to output stream.");
		}
	}
	
	@RequestMapping(value = "/clearAllCaches")
	@ResponseBody
	private String clearAllCaches() {
		cacheService.clearAllCaches();
		return "Cleared all caches from the CT Online Rating.";
	}
	
}
