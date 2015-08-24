package com.bc.ct.repository;

import java.util.List;

import com.bc.ct.beans.Location;
import com.google.common.base.Optional;

public interface GeographyRepository {
	
	public Location getLocation(String code);
	public List<Location> getAllLocations();
	public List<Location> getSpellCheckLocations(String city, String state, Optional<String> zip);
}
