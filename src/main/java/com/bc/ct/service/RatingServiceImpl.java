package com.bc.ct.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import com.bc.ct.util.XmlStringTransformer;
import com.bc.ct.ws.RateClient;
import com.bc.ct.ws.model.RateQuote;
import com.bc.ct.ws.model.RateQuote.Charges.Charge;
import com.bc.ct.ws.model.RateQuote.Leg;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;
import com.google.common.collect.ComparisonChain;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private RateClient rateClient;
	@Autowired
	private Jaxb2Marshaller marshaller;
	@Autowired
	private CurrencyService currencyService;
	
	private Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.bc.ct.service.RatingService#rate(com.bc.ct.ws.model.RateRequest)
	 */
	@Override
	public RateResponse rate(RateRequest rateRequest) {
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
		
		filterRoute(rateRequest.getRoute(), response.getQuotes());
		
		sortQuotes(response);
		modifyCurrency(rateRequest, response);
		
		return response;
	}
	
	private void filterRoute(String route, Collection<RateQuote> quotes) {
		if (!StringUtils.isEmpty(route)) {
			Iterator<RateQuote> iter = quotes.iterator();
			while (iter.hasNext()) {
				RateQuote quote = iter.next();
				if (!StringUtils.containsIgnoreCase(quote.getRoute(), route)){
					iter.remove();
				}
			}	
		}
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
	
	/**
	 * Rate is default in USD.  If they want it in another currency do the conversion here.
	 * @param request RateRequest
	 * @param response RateResponse
	 */
	private void modifyCurrency(RateRequest request, RateResponse response) {
		boolean intraCanadian = "CAN".equalsIgnoreCase(request.getOrigin().getNation()) &&
				"CAN".equalsIgnoreCase(request.getDest().getNation());
		if (intraCanadian) { //Rate from CT is already in CAD.  Just change the currency code
			for (RateQuote quote : response.getQuotes()){
				quote.setCurrency("CAD");
			}	
		}else if (!"USD".equalsIgnoreCase(request.getCurrency())) {
			DateTime shipDate = new DateTime(request.getShipDate());
			try {
				BigDecimal rate = currencyService.readCurrencyExchangeRate(shipDate.getYear(), shipDate.getMonthOfYear(), "USD", request.getCurrency());
				
				//Modify amounts based on currency exchange rate.
				for (RateQuote quote : response.getQuotes()){
					quote.setCurrency(request.getCurrency());
					quote.setTotalAmt(quote.getTotalAmt()*rate.floatValue());
					for (Leg leg : quote.getLeg()) {
						leg.setAmt(leg.getAmt()*rate.floatValue());
						for (com.bc.ct.ws.model.RateQuote.Leg.Charge charge : leg.getCharge()) {
							charge.setAmt(charge.getAmt()*rate.floatValue());
						}
					}
					for (Charge charge : quote.getCharges().getCharge()) {
						charge.setAmt(charge.getAmt()*rate.floatValue());
					}
				}
			}catch(IllegalArgumentException e) { //Unable to find rate, return from this method without modifying currency.
				return;
			}
		}
	}
}
