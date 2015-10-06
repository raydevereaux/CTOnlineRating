package com.bc.ct.service;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import com.bc.ct.util.XmlStringTransformer;
import com.bc.ct.ws.RateClient;
import com.bc.ct.ws.model.ClientGroup;
import com.bc.ct.ws.model.RateQuote;
import com.bc.ct.ws.model.RateQuote.Charges.Charge;
import com.bc.ct.ws.model.RateQuote.Leg;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateRequest.Stops;
import com.bc.ct.ws.model.RateResponse;
import com.google.common.collect.ComparisonChain;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private RateClient rateClient;
	@Autowired
	private Jaxb2Marshaller marshaller;
	
	private Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.bc.ct.service.RatingService#rate(com.bc.ct.ws.model.RateRequest)
	 */
	@Override
	public RateResponse rate(RateRequest rateRequest) {
		//Clear zip for BOISEW
		if (rateRequest != null && rateRequest.getClientGroup() != null && ClientGroup.BOISEW.equals(rateRequest.getClientGroup())) {
			rateRequest.getOrigin().setZip(null);
			rateRequest.getDest().setZip(null);
			for (Stops stops : rateRequest.getStops()) {
				stops.getStop().setZip(null);
			}
		}
		//Remove commodity desc before send and re-set it afterwards
		String commodityDesc = rateRequest.getCommoditys().get(0).getCommodity().getDesc();
		rateRequest.getCommoditys().get(0).getCommodity().setDesc(null);
		RateResponse response = rateClient.getRate(rateRequest);
		rateRequest.getCommoditys().get(0).getCommodity().setDesc(commodityDesc);

		//This should probably be done on Ray's web service but doing it here for now since I don't want to
		//change paper's mileage
		for (RateQuote quote : response.getQuotes()) {
			int miles = 0;
			for (Leg leg : quote.getLeg()) {
				miles+=leg.getMiles();
			}
			quote.setMiles(miles);
		}
		
		//Set the rateRequest and rateResponse raw Xml string
		response.setRateRequestXml(marshalObjectToXml(rateRequest));
		response.setRateResponseXml(marshalObjectToXml(response));
		
		sortQuotes(response);
		
		return response;
	}
	
	private String marshalObjectToXml(Object objectToMarshal) {
		String result = "";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshaller.getJaxbContext().createMarshaller().marshal(objectToMarshal, baos);
			result = XmlStringTransformer.prettifyXmlString(new String(baos.toByteArray()));
			baos.flush();
			baos.close();
		} catch (Exception e) {
			logger.error("Unable to marshal object to xml", e);
		}
		return result;
	}
	
	private void sortQuotes(RateResponse response) {
		//Sort quote by amount asc
		Collections.sort(response.getQuotes(), new Comparator<RateQuote>() {
			@Override
			public int compare(RateQuote o1, RateQuote o2) {
				return ComparisonChain.start().compare(o1.getTotalAmt(), o2.getTotalAmt()).result();
			}
		});
		//Sort charges by amount desc
		for (RateQuote quote : response.getQuotes()) {
			Collections.sort(quote.getCharges().getCharge(), new Comparator<Charge>() {
				@Override
				public int compare(Charge o1, Charge o2) {
					return ComparisonChain.start().compare(o2.getAmt(), o1.getAmt()).result();
				};
			});
		}
	}
}
