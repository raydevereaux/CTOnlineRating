package com.bc.ct.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsAuthoritiesPopulator implements	LdapAuthoritiesPopulator {

	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String userName) {

		Set<SimpleGrantedAuthority> userPerms = new HashSet<SimpleGrantedAuthority>();

		// get users permissions from service
		userPerms.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		return userPerms;
	}
}