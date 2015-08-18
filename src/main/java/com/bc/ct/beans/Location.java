package com.bc.ct.beans;

import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateRequest.Dest;
import com.bc.ct.ws.model.RateRequest.Origin;

public class Location {
	private String address1;
	private String address2;
    private String city;
    private String code;
    private String country;
    private String county;
    private String name;
    private String splc;
    private String state;
    private String zip;
    
	public String getAddress1() {
		return address1;
	}
	public String getAddress2() {
		return address2;
	}
	public String getCity() {
		return city;
	}
	public String getCode() {
		return code;
	}
	public String getCountry() {
		return country;
	}
	public String getCounty() {
		return county;
	}
	public String getName() {
		return name;
	}
	public String getSplc() {
		return splc;
	}
	public String getState() {
		return state;
	}
	public String getZip() {
		return zip;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSplc(String splc) {
		this.splc = splc;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public Dest toDest() {
		Dest dest = new Dest();
		dest.setCity(this.city);
		dest.setCode(this.code);
		dest.setCounty(this.county);
		dest.setNation(this.country);
		dest.setState(this.state);
		dest.setZip(this.zip);
		return dest;
	}
	public Origin toOrigin() {
		Origin origin = new Origin();
		origin.setCity(this.city);
		origin.setCode(this.code);
		origin.setCounty(this.county);
		origin.setNation(this.country);
		origin.setState(this.state);
		origin.setZip(this.zip);
		return origin;
	}
}
