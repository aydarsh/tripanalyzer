package com.rostertwo.trip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  
  private static final Logger log = LoggerFactory.getLogger(TripApiController.class);
  @Value("${BASICAUTHKEY:deafaultValue}")
  private String BASICAUTHKEY;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception
  {
    http
        .csrf().disable()
        .authorizeRequests().anyRequest().authenticated()
        .and()
        .httpBasic();
  }
  
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
  {
    log.info("BASICAUTHKEY value: {}", BASICAUTHKEY);
    auth.inMemoryAuthentication()
        .withUser("admin")
        .password("{noop}" + BASICAUTHKEY)
        .roles("USER");
  }
}