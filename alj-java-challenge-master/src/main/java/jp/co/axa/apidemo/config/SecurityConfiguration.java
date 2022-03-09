package jp.co.axa.apidemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Added security configuration for HTTP basic authentication
 *  Fetching user name and password details from application properties
 **/
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${login.security.user.name}")
	private String username;

	@Value("${login.security.user.password}")
	private String password;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		
		 http        
        .csrf().disable()
		.authorizeRequests()
		.antMatchers("/h2-console/**").permitAll()
        .mvcMatchers("/api/**").hasAnyRole("USER").anyRequest().fullyAuthenticated().and().httpBasic();
		 
		 http.headers().frameOptions().disable(); // Added to enable H2 console login
		 
	    // @formatter:on		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(username).password(passwordEncoder().encode(password)).roles("USER");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}