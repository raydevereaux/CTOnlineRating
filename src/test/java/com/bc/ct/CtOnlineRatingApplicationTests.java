package com.bc.ct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=CtOnlineRatingApplication.class, webEnvironment=WebEnvironment.NONE)
public class CtOnlineRatingApplicationTests {

	@Test
	public void contextLoads() {
	}
}
