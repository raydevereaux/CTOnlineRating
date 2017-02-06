package com.bc.ct.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Value("${ldap.url}")
	private String ldapUrl;
	@Value("${ldap.base}")
	private String ldapBase;
	@Value("${ldap.searchFilter}")
	private String ldapSearchFilter;
	@Value("${ldap.groupSearchBase}")
	private String ldapGroupSearchBase;
	@Value("${remember.me.secret}")
	private String REMEMBER_ME_SECRET;
	@Autowired
	private UserDetailsAuthoritiesPopulator authPopulator;
	
	@Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
    	contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setPooled(false);
        contextSource.afterPropertiesSet();
        return contextSource;
    }
	
	@Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
//			.inMemoryAuthentication()
//				.withUser("dda07o").password("test").authorities(new SimpleGrantedAuthority("ROLE_USER"));
			.ldapAuthentication()
			.userSearchBase(ldapGroupSearchBase)
			.userSearchFilter(ldapSearchFilter)
			.groupSearchBase(ldapGroupSearchBase)
			.contextSource()
			.url(ldapUrl + "/" + ldapBase)
			.and().ldapAuthoritiesPopulator(authPopulator);
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().authenticated()
                .and()
            .formLogin()
				.loginPage("/login").permitAll()
				.failureUrl("/login?error=true")
	        	.defaultSuccessUrl("/", false).permitAll()  // If this isn't set, when you logout and log back in it redirects to /login?logout=true
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
        		.logoutSuccessUrl("/login?logout=true")
                .permitAll();
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
		//Ignore static resources
        web.ignoring().antMatchers("/static/**", "/webjars/**", "/images/**", "/js/**", 
        		"/css/**", "/fonts/**", "/favicon.ico");
    }
}