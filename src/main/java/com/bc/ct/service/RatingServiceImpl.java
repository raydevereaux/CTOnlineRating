package com.bc.ct.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.ct.ws.RateClient;
import com.bc.ct.ws.model.ClientGroup;
import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;

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
		return response;
	}
}
