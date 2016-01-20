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

import com.bc.ct.beans.Commodity;

@Repository
public class CommodityRepositoryImpl implements CommodityRepository {

	@Autowired
	private JdbcTemplate uniJdbcTemplate;
	
	@Override
	@Cacheable("commodity")
	public List<Integer> getAllCommodityCodes() {
		String sql = "SELECT COMMODITY.CODE FROM NMFC ORDER BY COMMODITY.CODE";
		return uniJdbcTemplate.query(sql, new RowMapper<Integer>() {
			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		});
	}

	@Override
	@Cacheable("commodity")
	public List<Commodity> getCommodityList() {
		StringBuilder sql = new StringBuilder();
		sql.append("select COMMODITY.CODE, BOL.DESC, CLASS, DESCRIPTION ");
		sql.append("from NMFC ");
		sql.append("ORDER BY DESCRIPTION");
		//+" where CODE in ('"+code.toUpperCase()+"')"
		
		return uniJdbcTemplate.query(sql.toString(), new RowMapper<Commodity>() {
			@Override
			public Commodity mapRow(ResultSet rs, int rowNum) throws SQLException {
				Commodity comm = new Commodity();
				comm.setCode(StringUtils.trimWhitespace(rs.getString(1)));
				comm.setDesc(StringUtils.trimWhitespace(rs.getString(2)));
				comm.setCommClass(StringUtils.trimWhitespace(rs.getString(3)));
				comm.setDesc(StringUtils.trimWhitespace(rs.getString(4)));
				return comm;
			}
		});
	}

	@Cacheable("commodity")
	@Override
	public List<Commodity> getCommodityList(String client) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct b.COMMODITY.CODE, b.COMMODITY.DESCRIPTION, b.COMMODITY.CLASS ");
		sql.append("from ").append(client).append(".HIST a, ");
		sql.append(client).append(".HIST_COMM.INFO b ");
		sql.append("where a.@ID eq b.@ID ");
		sql.append("and b.COMMODITY.CODE IS NOT NULL ");
		sql.append("and b.COMMODITY.DESCRIPTION IS NOT NULL ");
		sql.append("and b.COMMODITY.CODE <> b.COMMODITY.DESCRIPTION ");
		if (!"BOISEB".equalsIgnoreCase(client)){
			sql.append("and a.J.RMK.CODE = 'BOL' ");
		}
		sql.append( "order by b.COMMODITY.DESCRIPTION");
		
		return uniJdbcTemplate.query(sql.toString(), new RowMapper<Commodity>() {
			@Override
			public Commodity mapRow(ResultSet rs, int rowNum) throws SQLException {
				Commodity comm = new Commodity();
				comm.setCode(StringUtils.trimWhitespace(rs.getString(1)));
				comm.setDesc(StringUtils.trimWhitespace(rs.getString(2)));
				comm.setCommClass(StringUtils.trimWhitespace(rs.getString(3)));
				return comm;
			}
		});
	}

}
