package com.bc.ct.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityWebAppInitializer extends AbstractSecurityWebApplicationInitializer
		implements WebApplicationInitializer {

}
