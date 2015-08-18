package com.bc.ct.ws;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;

public class RateClient extends WebServiceGatewaySupport {
	public RateResponse getRate(RateRequest request) {
		RateResponse response = (RateResponse)getWebServiceTemplate().marshalSendAndReceive(request);
		return response;
	}
}
