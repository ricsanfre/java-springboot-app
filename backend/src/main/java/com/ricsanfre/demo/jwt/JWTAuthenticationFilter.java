package com.ricsanfre.demo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil,
                                   UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Get JWT Token from request Header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Move on to next Filter
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization Header has the format "Bearer <jwt token>"
        // JWT token from character 7.
        String jwt = authHeader.substring(7);

        // Get subject from token
        String subject = jwtUtil.getSubject(jwt);

        // If subject is not null and User has not been authenticated
        // user is not is SecurityContext
        if (subject != null &&
                SecurityContextHolder.getContext().getAuthentication()==null) {
            // Get User Details
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            // Validate Token
            if(jwtUtil.isValidToken(jwt,userDetails.getUsername())){
                // If token is valid, create User Authentication context
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            // Move on to next Filter
            filterChain.doFilter(request,response);
        }
    }
}
