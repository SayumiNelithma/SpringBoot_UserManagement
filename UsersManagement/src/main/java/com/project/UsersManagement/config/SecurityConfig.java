package com.project.UsersManagement.config;

import com.project.UsersManagement.service.OurUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OurUserDetailsService ourUserDetailsService;
    @Autowired
    private JWTAuthFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection for stateless APIs
                .cors(Customizer.withDefaults())  // CORS with default settings
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**", "/public/**").permitAll()  // Public endpoints
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")  // Only Admin access for /admin/*
                        .requestMatchers("/user/**").hasAuthority("USER")  // Only User access for /user/*
                        .requestMatchers("/adminuser/**").hasAnyAuthority("ADMIN", "USER")  // Both Admin and User
                        .anyRequest().authenticated())  // All other requests need authentication
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session
                .authenticationProvider(authenticationProvider())  // Use the custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // JWT filter
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(ourUserDetailsService);  // Custom user details service
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());  // Password encoder
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // Use BCrypt for password encoding
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();  // Authentication manager bean
    }

}
