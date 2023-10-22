package com.ricsanfre.demo.security;

import com.ricsanfre.demo.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /*
                Disabling CSRF (Cross Site Request Forgery)   - We are not using HTML Forms
                https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#disable-csrf
                */
                .csrf((csrf) -> csrf.disable())
                // Authorize registerCustomer API without requiring authentication
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(HttpMethod.POST, "/api/v1/customer")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                // Specify Stateless sessions. Each request comes with authentication JWT token
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                // Add JWT Filter before UsernamePasswordAuthenticationFilter (used for form based authentication)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
