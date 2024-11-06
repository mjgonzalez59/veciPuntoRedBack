package com.puntored.veciapi.config;


import com.puntored.veciapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    /**
     * Followed the following Medium post
     * <a href="https://medium.com/@barbieri.santiago/basic-rest-api-security-with-spring-security-9f5d3a254af8"/a>
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable Cross-Site Request Forgery (POST methods don't work without this)
                .csrf(AbstractHttpConfigurer::disable)
                // Enable CORS
                .cors(Customizer.withDefaults())
                // Enable authorization requirement for requests
                .authorizeHttpRequests((requests) -> requests
                        // Allow unauthorized requests for "/api/suppliers" requests
                        .requestMatchers("/api/suppliers").permitAll()
                        // For any request (aside from the ones specified in the lines above)
                        // Allow requests from authenticated users
                        .anyRequest().authenticated()
                )
                // Configure Basic Auth (HEADER: Authorization=Basic <BASE64->user:password>)
                .httpBasic(Customizer.withDefaults())
                // Stateless Sessions for REST API
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDetails> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            users.add(
                    User.withDefaultPasswordEncoder() // TODO Change this for a production environment
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .build()
            );

        });
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Consider making this configurable
        configuration.setAllowedMethods(List.of("GET", "POST", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
