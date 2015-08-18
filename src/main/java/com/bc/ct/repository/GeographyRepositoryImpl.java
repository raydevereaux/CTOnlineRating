package com.bc.ct.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GeographyRepositoryImpl implements GeographyRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void testRead() {
		StringBuilder sql = new StringBuilder();
		sql.append("select @ID as keyId, COMMODITY.CODE, ");
		sql.append("BOL.DESC, CLASS, ");
		sql.append("DESCRIPTION from NMFC ");
		sql.append("where COMMODITY.CODE = 1 ");
		sql.append("order by DESCRIPTION");
		jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int col) throws SQLException {
				return "";
			}
		});
	}
}
