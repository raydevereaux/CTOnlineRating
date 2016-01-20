package com.bc.ct.controllers;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.ct.service.CurrencyService;

@Controller
public class CurrencyController {

	@Autowired
	private CurrencyService currencyService;
	
	@RequestMapping(value = "/readCurrency", method = RequestMethod.GET)
	@ResponseBody
	private BigDecimal readCurrency(@RequestParam String fromCurrency, @RequestParam String toCurrency,
			@RequestParam(required=false) Integer year, @RequestParam(required=false) Integer month) {
		if (year != null && month != null) 
			return currencyService.readCurrencyExchangeRate(year, month, fromCurrency, toCurrency);
		DateTime now = new DateTime();
		return currencyService.readCurrencyExchangeRate(now.getYear(), now.getMonthOfYear(), fromCurrency, toCurrency);
	}
}
