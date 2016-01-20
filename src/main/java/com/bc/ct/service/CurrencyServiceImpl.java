package com.bc.ct.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.ct.repository.CurrencyRepository;

@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private CurrencyRepository currencyRepo;
	
	@Override
	public BigDecimal readCurrencyExchangeRate(int year, int month, String fromCurrency, String toCurrency) {
		int tries = 0;
		int maxTries = 3;
		while(true) {
			DateTime date = new DateTime(year, month, 1, 0, 0);
			date = date.minusMonths(tries);
			BigDecimal rate = currencyRepo.readCurrencyExchangeRate(date.getYear(), date.getMonthOfYear(), fromCurrency, toCurrency);
			if (rate == null) { //Rate not found, try inverse.
				BigDecimal inverseRate = currencyRepo.readCurrencyExchangeRate(date.getYear(), date.getMonthOfYear(), toCurrency, fromCurrency);
				if (inverseRate != null && BigDecimal.ZERO.compareTo(inverseRate) != 0) {
					rate = BigDecimal.ONE.divide(inverseRate, inverseRate.scale(), BigDecimal.ROUND_HALF_UP);
				}
			}
			if (rate != null && BigDecimal.ZERO.compareTo(rate) == -1) {
				return rate;
			}
			if (++tries == maxTries) {
				throw new IllegalArgumentException(MessageFormatter.arrayFormat("No currency exchange rate found for {} to {} for Year {} and Month {}.", 
						new Object[] {fromCurrency, toCurrency, year, month}).getMessage());
			}
		}
	}
}
