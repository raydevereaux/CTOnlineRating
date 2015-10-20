package com.bc.ct.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

	@Bean
	@ConfigurationProperties(prefix="uni.db")
	public DataSource uniDBDatasource() throws SQLException {
		BasicDataSource ds = new BasicDataSource();
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() throws SQLException {
		return new JdbcTemplate(uniDBDatasource());
	}
}
