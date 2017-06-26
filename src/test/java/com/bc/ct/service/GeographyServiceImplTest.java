package com.bc.ct.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.bc.ct.beans.Location;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE)
public class GeographyServiceImplTest {

	@Autowired
	private GeographyService geoService;
	
	@Test
	@Ignore //This test doesn't quite work when run as part of a package
	public void testGetSpellCheckLocations() throws Exception {
		final String city = "AMERICAN CANYON";
		final String state = "CA";
		final String zip = "94589";
		List<Location> locations = geoService.getSpellCheckLocations(city, state, Optional.<String>absent());
		Function<Location, String> zipFunction = new Function<Location, String>() {
			@Override
			public String apply(Location input) {
				return input.getZip();
			}
		};
		assertThat(Iterables.transform(locations, zipFunction), hasItem(zip));
		Function<Location, String> cityFunction = new Function<Location, String>() {
			@Override
			public String apply(Location input) {
				return input.getCity();
			}
		};
		assertThat(Iterables.transform(locations, cityFunction), everyItem(equalTo(city)));
	}
}
