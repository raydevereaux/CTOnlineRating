package com.bc.ct.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.u2.jdbcx.UniJDBCDataSource;

@Configuration
@Profile("test")
public class DatabaseConfig {

	@Value("${unidb.account}")
	private String uniDBAccount;
	@Value("${unidb.serverHost}")
	private String uniDBServerHost;
	@Value("${unidb.username}")
	private String uniDBUser;
	@Value("${unidb.password}")
	private String uniDBPassword;
	
	@Bean
	public DataSource uniDBDatasource() throws SQLException {
		UniJDBCDataSource ds = new UniJDBCDataSource();
		ds.setAccount(uniDBAccount);
		ds.setServerHost(uniDBServerHost);
		ds.setUser(uniDBUser);
		ds.setPassword(uniDBPassword);
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() throws SQLException {
		return new JdbcTemplate(uniDBDatasource());
	}
}
