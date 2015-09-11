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
import com.google.common.base.Optional;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtOnlineRatingApplication.class)
public class GeographyRepositoryImplTest {

	@Autowired
	GeographyRepository repo;

	@Test
	public void testGetMillLocation() throws Exception {
		Location kf = repo.getMillLocation(Optional.<String>of("BOISEW"), "31");
		assertThat(kf, notNullValue());
		assertThat(kf.getCity(), IsEqualIgnoringCase.equalToIgnoringCase("KETTLE FALLS"));
		Location edmundston = repo.getMillLocation(Optional.<String>of("BOISEW"), "04R");
		assertThat(edmundston, notNullValue());
		assertThat(edmundston.getCity(), IsEqualIgnoringCase.equalToIgnoringCase("EDMUNDSTON"));
	}

	@Test
		public void testGetMillLocations() throws Exception {
			List<Location> allLocations = repo.getMillLocations(Optional.<String>absent());
			assertThat(allLocations, hasSize(greaterThan(250)));
		}

	@Test
	public void testGetSpellCheckLocations() throws Exception {
		List<Location> locations = repo.getSpellCheckLocations("BOISE", "ID", Optional.<String>absent());
		assertThat(locations, hasSize(1));
	}
}
