package com.malugu.springboot_starter_project.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.enums.UserAuthenticationType;
import tz.go.ega.uaa.repository.UserAccountRepository;

import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${ldap.login.url}")
	private String ldapUrl;

	@Value("${spring.ldap.base}")
	private String ldapBase;

	@Value("${ldap.userDn}")
	private String ldapUserDnPattern;

	@Value("${ldap.password-attribute}")
	private String ldapPasswordAttribute;

	@Value("${ldap.managerDn}")
	private String ldapGroupSearchBase;

	@Value("${ldap.userSearch}")
	private String ldapSearchFilter;

	@Value("${spring.ldap.password}")
	private String ldapPassword;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		Optional<UserAccount> userAccount = userAccountRepository.findFirstByUsername(username);

		if (userAccount.isPresent() && userAccount.get().getAuthenticationType() == UserAuthenticationType.Ldap) {
			return this.ldapAuthenticate(authentication);
		}
		Authentication auth = null;
		if (userAccount.isPresent() && passwordEncoder.matches(password, userAccount.get().getPassword())) {
			auth = new UsernamePasswordAuthenticationToken(username, password,
					userAccount.get().getAuthorities());
		} else if (userAccount.isPresent()
				&& userAccount.get().getAuthenticationType() == UserAuthenticationType.Both) {
			auth = this.ldapAuthenticate(authentication);
		} else {
			throw new BadCredentialsException("Invalid credentials");
		}
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public Authentication ldapAuthenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		LdapContextSource contextSource = createContextSource(ldapUrl);

		FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(ldapGroupSearchBase, ldapSearchFilter,
				contextSource);

		DefaultLdapAuthoritiesPopulator authoritiesPopulator = new DefaultLdapAuthoritiesPopulator(contextSource,
				ldapGroupSearchBase);
		authoritiesPopulator.setGroupSearchFilter(ldapSearchFilter);

		PasswordComparisonAuthenticator passwordAuthenticator = new PasswordComparisonAuthenticator(contextSource);
		passwordAuthenticator.setPasswordAttributeName(ldapPasswordAttribute);

		LdapAuthenticationProvider ldapProvider = new LdapAuthenticationProvider(passwordAuthenticator,
				authoritiesPopulator);
		ldapProvider.setUserDetailsContextMapper(new LdapUserDetailsMapper());

		return ldapProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	private LdapContextSource createContextSource(String ldapUrl) {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(ldapUrl);
		contextSource.setBase(ldapBase); // Make base DN dynamic if needed
		contextSource.setUserDn(ldapUserDnPattern);
		contextSource.setPassword(ldapPassword);
		contextSource.afterPropertiesSet();
		return contextSource;
	}
}
