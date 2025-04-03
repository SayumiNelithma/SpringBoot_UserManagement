package com.project.UsersManagement.config;

import com.project.UsersManagement.service.JWTUtils;
import com.project.UsersManagement.service.OurUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");  // Retrieve Authorization header
        final String jwtToken;
        final String userEmail;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // If not, pass the request along the filter chain
            return;
        }

        // Extract JWT token (strip "Bearer " prefix)
        jwtToken = authHeader.substring(7); 
        userEmail = jwtUtils.extractUsername(jwtToken);  // Extract username (email)

        // If the username is not null and no authentication is set in the SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load UserDetails (which in your case is an instance of OurUsers)
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail);

            // If the token is valid, set up the authentication in the SecurityContext
            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                // Create UsernamePasswordAuthenticationToken and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);  // Set the context globally
            }
        }
        filterChain.doFilter(request, response);  // Pass the request along the filter chain
    }
}
