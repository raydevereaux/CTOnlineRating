package com.bc.ct.service;

import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.ct.ws.RateClient;
import com.bc.ct.ws.model.ClientGroup;
import com.bc.ct.ws.model.RateQuote;
import com.bc.ct.ws.model.RateQuote.Charges.Charge;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;
import com.google.common.collect.ComparisonChain;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private RateClient rateClient;
	
	/* (non-Javadoc)
	 * @see com.bc.ct.service.RatingService#rate(com.bc.ct.ws.model.RateRequest)
	 */
	@Override
	public RateResponse rate(RateRequest rateRequest) {
		//Clear zip for BOISEW
		if (rateRequest != null && rateRequest.getClientGroup() != null && ClientGroup.BOISEW.equals(rateRequest.getClientGroup())) {
			rateRequest.getDest().setZip(null);	
		}
		//Remove commodity desc before send and re-set it afterwards
		String commodityDesc = rateRequest.getCommoditys().get(0).getCommodity().getDesc();
		rateRequest.getCommoditys().get(0).getCommodity().setDesc(null);
		RateResponse response = rateClient.getRate(rateRequest);
		rateRequest.getCommoditys().get(0).getCommodity().setDesc(commodityDesc);
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
		return response;
	}
}
