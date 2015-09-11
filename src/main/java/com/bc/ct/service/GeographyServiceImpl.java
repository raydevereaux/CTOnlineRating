package com.bc.ct.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.Location;
import com.bc.ct.repository.GeographyRepository;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Service
public class GeographyServiceImpl implements GeographyService {

	private Logger logger = LoggerFactory.getLogger(GeographyServiceImpl.class);
	
	@Autowired
	private GeographyRepository repo;
	
	@Override
	@Cacheable("geography")
	public Location getMillLocation(Optional<String>client, String code) {
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("Location code cannot be empty");
		}
		try {
			return repo.getMillLocation(client, code);	
		}catch(EmptyResultDataAccessException e) {
			if (client.isPresent()) {
				return repo.getMillLocation(Optional.<String>absent(), code); 
			}else {
				throw e;
			}
		}
	}

	@Override
	@Cacheable("geography")
	public List<Location> getAllMillLocations(Optional<String> client) {
		List<Location> locations = Lists.newArrayList();
		if (client.isPresent()) {
			locations.addAll(repo.getMillLocations(client));
		}
		locations.addAll(repo.getMillLocations(Optional.<String>absent()));
		return locations;
	}
	
	@Override
	@Cacheable("geography")
	public List<Location> getSpellCheckLocations(final String city, final String state, final Optional<String> zip) {
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

		//Only return those locations that match the city, state, and zip if present
		returnLocs = Lists.newArrayList(Iterables.filter(returnLocs, new Predicate<Location>() {
			@Override
			public boolean apply(Location input) {
				return input.getCity().contains(city) && input.getState().contains(state) &&
						(zip.isPresent() ? zip.get().equals(input.getZip()) : true);
			}
		}));
		
		//Sort ignoring space on the city.  This keeps El Paso Fur lower than El Paso on the returned list.
		Collections.sort(returnLocs, new Comparator<Location>() {
			@Override
			public int compare(Location o1, Location o2) {
				return ComparisonChain.start().compare(StringUtils.trimAllWhitespace(o1.getCity()), StringUtils.trimAllWhitespace(o2.getCity()))
						.compare(o1.getState(),  o2.getState()).compare(o1.getZip(), o2.getZip())
						.compare(o1.getCounty(), o2.getCounty()).result();
			}
		});
		
		return returnLocs;
	}
}
