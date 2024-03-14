package edu.ipp.isep.dei.dimei.retailproject.config;

import edu.ipp.isep.dei.dimei.retailproject.domain.enums.RoleEnum;
import edu.ipp.isep.dei.dimei.retailproject.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String[] WHITELIST_URL = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/auth",
            "/auth/**"
    };
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(WHITELIST_URL).permitAll()
                        // Categories Controller
                        .requestMatchers(GET, "/categories/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(POST, "/categories").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, "/categories/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(DELETE, "/categories/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Item Controller
                        .requestMatchers(GET, "/items/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, "/items").hasAnyAuthority(RoleEnum.MERCHANT.name())
                        .requestMatchers(GET, "/items/{id}").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(POST, "items").hasAnyAuthority(RoleEnum.MERCHANT.name())
                        .requestMatchers(DELETE, "items/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, "items/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Merchants Controller
                        .requestMatchers(GET, "/merchants/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(POST, "/merchants").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, "/merchants/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(DELETE, "/merchants/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Merchant Order Controller
                        .requestMatchers(GET, "/merchantorders/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, "/merchantorders").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(GET, "/merchantorders/{id}").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(PATCH, "merchantorders/**").hasAnyAuthority(RoleEnum.MERCHANT.name())
                        // Order Controller
                        .requestMatchers(GET, "/orders/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, "/orders").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(GET, "/orders/{id}").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(POST, "/orders").hasAnyAuthority(RoleEnum.USER.name())
                        .requestMatchers(DELETE, "/orders/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, "/orders/{id}/cancel").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(PATCH, "/orders/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Shipping Order Controller
                        .requestMatchers(GET, "/shippingorders/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, "/shippingorders").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(GET, "/shippingorders/{id}").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(PATCH, "shippingorders/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
