package com.bc.ct.service;

import com.bc.ct.ws.model.RateRequest;
import com.bc.ct.ws.model.RateResponse;

public interface RatingService {

	RateResponse rate(RateRequest rateRequest);

}