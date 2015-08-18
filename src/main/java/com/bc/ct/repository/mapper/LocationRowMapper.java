package com.bc.ct.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.bc.ct.beans.Location;

public class LocationRowMapper implements RowMapper<Location> {
	@Override
	public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
		Location location = new Location();
		location.setCode(StringUtils.trimWhitespace(rs.getString(1)));
		location.setName(StringUtils.trimWhitespace(rs.getString(2)));
		location.setAddress1(StringUtils.trimWhitespace(rs.getString(3)));
		location.setAddress2(StringUtils.trimWhitespace(rs.getString(4)));
		location.setCity(StringUtils.trimWhitespace(rs.getString(5)));
		location.setState(StringUtils.trimWhitespace(rs.getString(6)));
		location.setZip(StringUtils.trimWhitespace(rs.getString(7)));
		location.setCounty(StringUtils.trimWhitespace(rs.getString(8)));
		location.setCountry(StringUtils.trimWhitespace(rs.getString(9)));
		location.setSplc(StringUtils.trimWhitespace(rs.getString(10)));
		return location;
	}
}
