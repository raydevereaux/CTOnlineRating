package com.bc.ct.repository;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bc.ct.CtOnlineRatingApplication;
import com.bc.ct.beans.Location;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtOnlineRatingApplication.class)
public class GeographyRepositoryImplTest {

	@Autowired
	GeographyRepository repo;

	@Test
	public void testGetLocation() throws Exception {
		Location location = repo.getLocation("31");
		assertThat(location, notNullValue());
		assertThat(location.getCity(), IsEqualIgnoringCase.equalToIgnoringCase("KETTLE FALLS"));
	}

	@Test
	public void testGetAllLocations() throws Exception {
		List<Location> allLocations = repo.getAllLocations();
		assertThat(allLocations, hasSize(greaterThan(250)));
	}
}
