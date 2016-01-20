package com.bc.ct.repository;

import java.math.BigDecimal;

public interface CurrencyRepository {

	public BigDecimal readCurrencyExchangeRate(int year, int month, String fromCurrency, String toCurrency);
}
