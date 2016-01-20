package com.bc.ct.service;

import java.math.BigDecimal;

public interface CurrencyService {
	public BigDecimal readCurrencyExchangeRate(int year, int month, String fromCurrency, String toCurrency);
}
