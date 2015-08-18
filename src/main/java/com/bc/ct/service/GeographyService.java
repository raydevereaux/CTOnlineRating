package com.bc.ct.service;

import java.util.List;

import com.bc.ct.beans.Location;

public interface GeographyService {
	
	public Location getLocation(String code);
	public List<Location> getAllLocations();
}
