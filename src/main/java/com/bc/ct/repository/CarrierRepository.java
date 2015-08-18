package com.bc.ct.repository;

import java.util.List;

import com.google.common.base.Optional;

public interface CarrierRepository {

	public List<String> getCarrierList(Optional<String> client);
}
