package com.bc.ct.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.Location;
import com.bc.ct.repository.GeographyRepository;

@Service
public class GeographyServiceImpl implements GeographyService {

	@Autowired
	private GeographyRepository repo;
	
	@Override
	public Location getLocation(String code) {
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("Location code cannot be empty");
		}
		return repo.getLocation(code);
	}

	@Override
	public List<Location> getAllLocations() {
		return repo.getAllLocations();
	}
}
