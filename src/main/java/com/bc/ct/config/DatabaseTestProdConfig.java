package com.bc.ct.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Profile("!local")
public class DatabaseTestProdConfig {

	@Value("${spring.uni.db.jndi-name}")
	private String uniJndiName;
	@Value("${spring.ds_mom.jndi-name}")
	private String momJndiName;

	@Bean
	public JdbcTemplate uniJdbcTemplate() throws SQLException {
		return new JdbcTemplate(uniDBDatasource());
	}
	
	@Bean
	public DataSource uniDBDatasource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		lookup.setResourceRef(true);
		return lookup.getDataSource(uniJndiName);
	}
	
	@Bean
	public JdbcTemplate momJdbcTemplate() {
		return new JdbcTemplate(momDataSource());
	}
	
	@Bean
	@Primary
	public DataSource momDataSource() {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		lookup.setResourceRef(true);
		return lookup.getDataSource(momJndiName);
	}
}
