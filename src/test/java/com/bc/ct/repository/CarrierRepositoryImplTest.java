package com.bc.ct.repository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE)
public class CarrierRepositoryImplTest {

	@Autowired
	private CarrierRepository repo;
	
	@Test
	public void testGetCarrierList() throws Exception {
		String client = "BOISEW";
		List<String> carrierList = repo.getCarrierList(Optional.of(client));
		assertThat(carrierList, hasSize(greaterThan(0)));
		assertThat(carrierList, everyItem(containsString(client)));
		System.out.println(carrierList);
	}
}
