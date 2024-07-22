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
    private static final String CATEGORIES_PATH = "/categories";
    private static final String ITEMS_PATH = "/items";
    private static final String MERCHANTS_PATH = "/merchants";
    private static final String MERCHANT_ORDERS_PATH = "/merchantorders";
    private static final String ORDERS_PATH = "/orders";
    private static final String SHIPPING_ORDERS_PATH = "/shippingorders";
    private static final String ID_PATH = "/{id}";
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(WHITELIST_URL).permitAll()
                        // Categories Controller
                        .requestMatchers(GET, CATEGORIES_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(POST, CATEGORIES_PATH).hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, CATEGORIES_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(DELETE, CATEGORIES_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Item Controller
                        .requestMatchers(GET, ITEMS_PATH + "/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, ITEMS_PATH).hasAnyAuthority(RoleEnum.MERCHANT.name())
                        .requestMatchers(GET, ITEMS_PATH + ID_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(POST, ITEMS_PATH).hasAnyAuthority(RoleEnum.MERCHANT.name())
                        .requestMatchers(DELETE, ITEMS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, ITEMS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Merchants Controller
                        .requestMatchers(GET, MERCHANTS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(POST, MERCHANTS_PATH).hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, MERCHANTS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(DELETE, MERCHANTS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Merchant Order Controller
                        .requestMatchers(GET, MERCHANT_ORDERS_PATH + "/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, MERCHANT_ORDERS_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(GET, MERCHANT_ORDERS_PATH + ID_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.MERCHANT.name())
                        .requestMatchers(PATCH, MERCHANT_ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.MERCHANT.name())
                        // Order Controller
                        .requestMatchers(GET, ORDERS_PATH + "/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, ORDERS_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(GET, ORDERS_PATH + ID_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(POST, ORDERS_PATH).hasAnyAuthority(RoleEnum.USER.name())
                        .requestMatchers(DELETE, ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(PATCH, ORDERS_PATH + "/{id}/cancel").hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(PATCH, ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        // Shipping Order Controller
                        .requestMatchers(GET, SHIPPING_ORDERS_PATH + "/all").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(GET, SHIPPING_ORDERS_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(GET, SHIPPING_ORDERS_PATH + ID_PATH).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .requestMatchers(PATCH, SHIPPING_ORDERS_PATH + "/**").hasAnyAuthority(RoleEnum.ADMIN.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
