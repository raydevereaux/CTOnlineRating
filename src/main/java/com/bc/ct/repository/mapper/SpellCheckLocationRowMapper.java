package com.bc.ct.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.Location;

public class SpellCheckLocationRowMapper implements RowMapper<Location> {
	@Override
	public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
		Location location = new Location();
		location.setCity(StringUtils.trimWhitespace(rs.getString(1)));
		location.setState(StringUtils.trimWhitespace(rs.getString(2)));
		location.setZip(StringUtils.trimWhitespace(rs.getString(3)));
		location.setCounty(StringUtils.trimWhitespace(rs.getString(4)));
		location.setCountry(StringUtils.trimWhitespace(rs.getString(5)));
		location.setSplc(StringUtils.trimWhitespace(rs.getString(6)));
		return location;
	}
}
