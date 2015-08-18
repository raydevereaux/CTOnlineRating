package com.bc.ct.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.ct.repository.CarrierRepository;
import com.google.common.base.Optional;

@Service
public class CarrierServiceImpl implements CarrierService {

	@Autowired CarrierRepository repo;
	
	@Override
	public List<String> getCarrierList(Optional<String> client) {
		return repo.getCarrierList(client);
	}
}
