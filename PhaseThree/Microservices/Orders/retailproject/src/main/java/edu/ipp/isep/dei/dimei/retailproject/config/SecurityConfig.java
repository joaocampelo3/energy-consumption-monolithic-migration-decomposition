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

    private static final String ORDERS_PATH = "/orders";
    private static final String ID_PATH = "/{id}";
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(WHITELIST_URL).permitAll()
                        // Order Controller
                        .requestMatchers(GET, ORDERS_PATH + "/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, ORDERS_PATH).hasAnyAuthority(RoleEnum.USER.name())
                        .requestMatchers(GET, ORDERS_PATH + ID_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(POST, ORDERS_PATH).hasAnyAuthority(RoleEnum.USER.name())
                        .requestMatchers(DELETE, ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, ORDERS_PATH + "/{id}/cancel").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(PATCH, ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
