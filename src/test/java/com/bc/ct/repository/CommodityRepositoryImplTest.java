package com.bc.ct.repository;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bc.ct.CtOnlineRatingApplication;
import com.bc.ct.beans.Commodity;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtOnlineRatingApplication.class)
public class CommodityRepositoryImplTest {

	@Autowired
	private CommodityRepository repo;

	@Test
	public void testGetAllCommodityCodes() throws Exception {
		List<Integer> commodityCodes = repo.getAllCommodityCodes();
		assertThat(commodityCodes, hasSize(greaterThan(150)));
	}

	@Test
	public void testGetCommodityList() throws Exception {
		List<Commodity> commodityList = repo.getCommodityList();
		assertThat(commodityList, hasSize(greaterThan(150)));
	}
	
	@Test
	public void testGetCommodityListWithClient() throws Exception {
		List<Commodity> commodityList = repo.getCommodityList("BOISEW");
		assertThat(commodityList, hasSize(greaterThan(40)));
	}
}
