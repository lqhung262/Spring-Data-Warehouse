package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/actuator/health",
            "/auth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtEnabledFilter jwtEnabledFilter, PermissionAuthorizationFilter permissionAuthorizationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2
                        -> oauth2.jwt(jwt
                        -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        // register the enabled-check filter after the bearer token filter
        http.addFilterAfter(jwtEnabledFilter, BearerTokenAuthenticationFilter.class);
        // register permission auth filter after enabled filter
        http.addFilterAfter(permissionAuthorizationFilter, JwtEnabledFilter.class);

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("roles"); // mình sẽ đọc roles từ converter phụ bên dưới

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
            Collection<String> roles =
                    realmAccess != null
                            ? (Collection<String>) realmAccess.getOrDefault("roles", Collections.emptyList())
                            : Collections.emptyList();
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return jwtConverter;
    }
}
