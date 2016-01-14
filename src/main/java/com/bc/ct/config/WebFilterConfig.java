package com.bc.ct.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Setting Cache-Control on all fonts so they are displayed properly over HTTPS. See
 * https://github.com/FortAwesome/Font-Awesome/issues/6454
 * @author dda07o
 *
 */
@Configuration
public class WebFilterConfig {

	@Bean
	public FilterRegistrationBean filterRegistration() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.addUrlPatterns("/fonts/*");
		bean.setFilter(filter());
		return bean;
	}
	
	public Filter filter() {
		return new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {}
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate");
				httpResponse.setHeader("Pragma", "");
			    chain.doFilter(request, httpResponse);
			}
			
			@Override
			public void destroy() {}
		};
	}
}
