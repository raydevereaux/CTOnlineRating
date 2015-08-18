package com.bc.ct.service;

import java.util.List;

import com.google.common.base.Optional;

public interface CarrierService {

	public List<String> getCarrierList(Optional<String> client);
}
