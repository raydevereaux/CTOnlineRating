package com.bc.ct.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@Repository
public class CarrierRepositoryImpl implements CarrierRepository {

	@Autowired
	private JdbcTemplate uniJdbcTemplate;
	
	@Cacheable("carrier")
	@Override
	public List<String> getCarrierList(Optional<String> client) {
		StringBuilder sql = new StringBuilder();
		sql.append("select @ID as keyId from CARRIER.LIST");
		List<String> carrierList = uniJdbcTemplate.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return StringUtils.trimWhitespace(rs.getString(1));
			}
		});
		if (client.isPresent()) {
			return Lists.newArrayList(Collections2.filter(carrierList, Predicates.containsPattern(client.get())));
		}
		return carrierList;
	}

}
