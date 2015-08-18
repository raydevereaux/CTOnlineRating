package com.bc.ct.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bc.ct.CtOnlineRatingApplication;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtOnlineRatingApplication.class)
@WebAppConfiguration
public class GeographyRepositoryImplTest {

	@Autowired
	GeographyRepository repo;
	
	@Test
	public void testTestRead() throws Exception {
		repo.testRead();
	}

}
