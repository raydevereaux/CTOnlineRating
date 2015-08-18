package com.bc.ct.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.bc.ct.ws.RateClient;

@Configuration
public class WebServiceConfig {
	
	@Value("${webServiceUrl}")
	private String webServiceUrl;
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.bc.ct.ws.model");
		return marshaller;
	}

	@Bean
	public RateClient rateClient(Jaxb2Marshaller marshaller) {
		RateClient client = new RateClient();
		client.setDefaultUri(webServiceUrl);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
}
