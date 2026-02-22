package com.blog.config;

import com.blog.constants.ApiRoutes;
import com.blog.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/images/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET,  ApiRoutes.ARTICLES + "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, ApiRoutes.COMMENTS + "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.API + "/articles/*/comments")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.COMMENTS + "/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.COMMENTS + "/**")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.ARTICLES, ApiRoutes.ARTICLES + "/*/cover-image")
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.ARTICLES + "/**")
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.ARTICLES + "/**")
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}