package com.bc.ct.repository;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE)
public class CurrencyRepositoryImplTest {

	@Autowired
	private CurrencyRepository repo;
	
	@Test
		public void testReadCurrencyExchangeRate() throws Exception {
			DateTime now = new DateTime();
			BigDecimal rate = repo.readCurrencyExchangeRate(now.getYear(), now.getMonthOfYear(), null, null);
			assertThat(rate, greaterThan(BigDecimal.ZERO));
		}
}
