package com.bc.ct.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bc.ct.beans.Location;
import com.bc.ct.repository.mapper.LocationRowMapper;
import com.bc.ct.repository.mapper.SpellCheckLocationRowMapper;
import com.google.common.base.Optional;

@Repository
public class GeographyRepositoryImpl implements GeographyRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Cacheable("geography")
	@Override
	public Location getLocation(String code) {
		StringBuilder sql = new StringBuilder();
		sql.append("select CODE, ");
		sql.append("NAME, ");
		sql.append("ADDRESS1, ");
		sql.append("ADDRESS2, ");
		sql.append("CITY, ");
		sql.append("STATE, ");
		sql.append("ZIP, ");
		sql.append("COUNTY, ");
		sql.append("COUNTRY, ");
		sql.append("SPLC ");
		sql.append("from LOCATION.CODES ");
		sql.append("where CODE = ?");
		
		return jdbcTemplate.queryForObject(sql.toString(), new Object[] {code}, new LocationRowMapper());
	}
	
	@Override
	public List<Location> getAllLocations() {
		StringBuilder sql = new StringBuilder();
		sql.append("select CODE, ");
		sql.append("NAME, ");
		sql.append("ADDRESS1, ");
		sql.append("ADDRESS2, ");
		sql.append("CITY, ");
		sql.append("STATE, ");
		sql.append("ZIP, ");
		sql.append("COUNTY, ");
		sql.append("COUNTRY, ");
		sql.append("SPLC ");
		sql.append("from LOCATION.CODES");
		
		return jdbcTemplate.query(sql.toString(), new LocationRowMapper());
	}
	
	@Override
	public List<Location> getSpellCheckLocations(String city, String state, Optional<String> zip) {
		StringBuilder sql = new StringBuilder();
		sql.append("select CITY, ");
		sql.append("STATE, ");
		sql.append("ZIP, ");
		sql.append("COUNTY, ");
		sql.append("COUNTRY, ");
		sql.append("SPLC ");
		sql.append("from SPELLCHECK where CITY = 'DENVER'");
		
		return jdbcTemplate.query(sql.toString(), new SpellCheckLocationRowMapper());
	}
}
