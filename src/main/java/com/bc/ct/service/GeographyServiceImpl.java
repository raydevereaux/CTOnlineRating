package com.bc.ct.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.Location;
import com.bc.ct.repository.GeographyRepository;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Service
public class GeographyServiceImpl implements GeographyService {

	private Logger logger = LoggerFactory.getLogger(GeographyServiceImpl.class);
	
	@Autowired
	private GeographyRepository repo;
	
	@Override
	public Location getMillLocation(String code) {
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("Location code cannot be empty");
		}
		return repo.getMillLocation(code);
	}

	@Override
	public List<Location> getAllMillLocations() {
		return repo.getAllMillLocations();
	}
	
	@Override
	public List<Location> getSpellCheckLocations(String city, String state, final Optional<String> zip) {
		List<Location> locs = repo.getSpellCheckLocations(city, state, zip);
		List<Location> returnLocs = Lists.newArrayList();
		String delimiter = "�";
		for (Location loc : locs) {
			String[] cities = loc.getCity().split(delimiter);
			String[] states = loc.getState().split(delimiter);
			String[] counties = loc.getCounty().split(delimiter);
			String[] splcs = loc.getSplc().split(delimiter);
			String[] zips = loc.getZip().split(delimiter);
			String[] countries = loc.getCountry().split(delimiter);
			for (int i=0; i<cities.length; i++) {
				Location location = new Location();
				try {
					location.setCity(cities[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check city.", e);
				}
				try {
					location.setState(states[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check state.", e);
				}
				try {
					location.setSplc(splcs[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check splc.", e);
				}
				try {
					location.setCounty(counties[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check county.", e);
				}
				try {
					location.setCountry(countries[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check country.", e);
				}
				try {
					location.setZip(zips[i]);
				}catch(ArrayIndexOutOfBoundsException e) {
					logger.debug("Error while parsing spell check zip.", e);
				}
				if (!StringUtils.isEmpty(location.getCity()) && !StringUtils.isEmpty(location.getState())) {
					returnLocs.add(location);	
				}
			}
		}
		
		if (zip.isPresent()) {
			returnLocs = Lists.newArrayList(Iterables.filter(returnLocs, new Predicate<Location>() {
				@Override
				public boolean apply(Location input) {
					return zip.get().equals(input.getZip());
				}
			}));	
		}
		return returnLocs;
	}
}
