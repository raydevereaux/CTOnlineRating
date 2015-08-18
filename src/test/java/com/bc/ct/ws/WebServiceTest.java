package com.bc.ct.ws;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bc.ct.CtOnlineRatingApplication;
import com.bc.ct.ws.model.ClientGroup;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateRequest.Commoditys;
import com.bc.ct.ws.model.RateRequest.Commoditys.Commodity;
import com.bc.ct.ws.model.RateRequest.Dest;
import com.bc.ct.ws.model.RateRequest.Equipment;
import com.bc.ct.ws.model.RateRequest.Origin;
import com.bc.ct.ws.model.RateResponse;
import com.bc.ct.ws.model.ShipMode;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CtOnlineRatingApplication.class)
@WebAppConfiguration
public class WebServiceTest {
	
	@Autowired
	private RateClient rateClient;
	
	private RateRequest getRateRequest() {
		RateRequest request = new RateRequest();
    	request.setClientGroup(ClientGroup.BOISEW);
    	request.setShipMode(ShipMode.TRUCK);
    	Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, 2013);
    	cal.set(Calendar.MONTH, Calendar.AUGUST);
    	cal.set(Calendar.DATE, 26);
    	request.setShipDate(cal.getTime());
    	Origin origin = new Origin();
    	origin.setCode("53"); 
    	origin.setCity("MEDFORD");
    	origin.setState("OR");
//    	origin.setZip("97501");
    	request.setOrigin(origin);
    	Dest dest = new Dest();
    	dest.setCity("AMERICAN CANYON");
    	dest.setState("CA");
    	request.setDest(dest);
    	Commodity comm = new Commodity();
    	comm.setCode("2432158");
    	comm.setWgt(47655);
    	Commoditys comms = new Commoditys();
    	comms.setCommodity(comm);
    	request.getCommoditys().add(comms);
    	request.setEquipment(new Equipment());
		
		return request;
	}
	
	@Test
	public void testGetRate() {
		RateResponse response = rateClient.getRate(getRateRequest());
		assertThat(response, notNullValue());
		assertThat(response.getQuotes(), allOf(notNullValue(), not(empty())));
		assertThat(response.getQuotes(), hasSize(2));
		assertThat(BigDecimal.valueOf(response.getQuotes().get(0).getTotalAmt()), 
				closeTo(new BigDecimal("750"), new BigDecimal("50")));
	}
}
