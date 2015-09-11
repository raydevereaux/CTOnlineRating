package com.bc.ct.service;

import java.util.List;

import com.bc.ct.beans.Location;
import com.google.common.base.Optional;

public interface GeographyService {
	
	public Location getMillLocation(Optional<String>client, String code);
	public List<Location> getAllMillLocations(Optional<String> client);
	public List<Location> getSpellCheckLocations(String city, String state, Optional<String> zip);
}
