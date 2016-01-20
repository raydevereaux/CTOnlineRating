package com.bc.ct.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("local")
public class DatabaseConfigLocal {
		
	@Bean
	public JdbcTemplate momJdbcTemplate() {
		return new JdbcTemplate(momDataSource());
	}
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix="spring.ds_mom")
	public DataSource momDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public JdbcTemplate uniJdbcTemplate() throws SQLException {
		return new JdbcTemplate(uniDBDatasource());
	}
	
	@Bean
	@ConfigurationProperties(prefix="uni.db")
	public DataSource uniDBDatasource() throws SQLException {
		BasicDataSource ds = new BasicDataSource();
		return ds;
	}
}
