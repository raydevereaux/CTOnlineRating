package com.bc.ct.repository;

import java.util.List;

import com.bc.ct.beans.Location;

public interface GeographyRepository {
	
	public Location getLocation(String code);
	public List<Location> getAllLocations();
}
