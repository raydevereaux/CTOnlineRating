package com.bc.ct.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.service.CarrierService;
import com.google.common.base.Optional;

@Controller
public class CarrierController {

	@Autowired
	private CarrierService carrierService;
	
	@GetMapping("/carrierList.json")
	@ResponseBody
	private List<String> getCarrierList(@RequestParam(required=false) String client) {
		return carrierService.getCarrierList(Optional.<String>fromNullable(client));
	}
}
