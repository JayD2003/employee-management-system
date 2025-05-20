package com.emp_mgmt_sys.config;

import com.emp_mgmt_sys.security.CustomAccessDeniedHandler;
import com.emp_mgmt_sys.security.CustomAuthenticationEntryPoint;
import com.emp_mgmt_sys.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;


    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;


    // Constructor injection for required dependencies
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT)
                .csrf(csrf -> csrf.disable())

                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

//                        // Role-based endpoints
//                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
//                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                // Stateless session (required for JWT)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Add JWT filter before Spring Security's default filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
