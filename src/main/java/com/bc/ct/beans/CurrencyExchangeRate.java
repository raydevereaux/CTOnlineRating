package com.bc.ct.beans;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public class CurrencyExchangeRate {
	private int year;
	private int month;
	private String currencyCd;
	private String fromCurrencyCd;
	private BigDecimal currentMonthRate;
	private BigDecimal prevMonthRate;
	private String lastUpdtUid;
	private DateTime lastUpdtTs;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public String getCurrencyCd() {
		return currencyCd;
	}
	public void setCurrencyCd(String currencyCd) {
		this.currencyCd = currencyCd;
	}
	public String getFromCurrencyCd() {
		return fromCurrencyCd;
	}
	public void setFromCurrencyCd(String fromCurrencyCd) {
		this.fromCurrencyCd = fromCurrencyCd;
	}
	public BigDecimal getCurrentMonthRate() {
		return currentMonthRate;
	}
	public void setCurrentMonthRate(BigDecimal currentMonthRate) {
		this.currentMonthRate = currentMonthRate;
	}
	public BigDecimal getPrevMonthRate() {
		return prevMonthRate;
	}
	public void setPrevMonthRate(BigDecimal prevMonthRate) {
		this.prevMonthRate = prevMonthRate;
	}
	public String getLastUpdtUid() {
		return lastUpdtUid;
	}
	public void setLastUpdtUid(String lastUpdtUid) {
		this.lastUpdtUid = lastUpdtUid;
	}
	public DateTime getLastUpdtTs() {
		return lastUpdtTs;
	}
	public void setLastUpdtTs(DateTime lastUpdtTs) {
		this.lastUpdtTs = lastUpdtTs;
	}
	
}
