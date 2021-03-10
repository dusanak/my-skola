package com.skillsfighters.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${firebase.debug.token:#{null}}")
    private Optional<String> firebaseDebugToken;

    public static final String ROLE_USER = "USER";

    @Autowired
    private FirebaseAuthenticationProvider firebaseAuthenticationProvider;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(firebaseAuthenticationProvider);
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //cross site request forgery protection is disabled,
                //other applications can try to attack if user is authenticated
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/user/delete", "/user/update", "/user/show", "/group/**", "/activity/**", "/token/**", "/uioptions/**").hasRole(ROLE_USER)
                .and()
                .addFilterBefore(firebaseDebugToken.isPresent() ? new FirebaseAuthenticationFilter(firebaseDebugToken) : new FirebaseAuthenticationFilter(),
                        BasicAuthenticationFilter.class)
                //stateless means that every action needs to be authenticated
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.
    }
}
