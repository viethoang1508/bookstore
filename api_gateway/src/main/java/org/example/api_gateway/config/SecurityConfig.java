package org.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /*
     Security cho API Gateway (Spring Cloud Gateway + WebFlux).

     Mục tiêu:
     1) Chỉ public đúng các endpoint cần public (auth, catalog, promotions public).
     2) Endpoint /admin/** chỉ cho ROLE_ADMIN.
     3) Endpoint /users/** chỉ cho user đã đăng nhập (ROLE_USER hoặc ROLE_ADMIN).
     4) Endpoint /internal/** chỉ cho service-to-service permitAll
     5) Tất cả endpoint khác mặc định yêu cầu authenticate để tránh bỏ sót quyền truy cập.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Gateway dùng JWT Bearer token (stateless), không dùng session/cookie form,
                // nên không cần cơ chế CSRF token.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeExchange(exchanges  -> exchanges
                        // 1) PUBLIC ENDPOINTS
                        // Auth service: đăng ký / đăng nhập.
                        .pathMatchers("/public/auth/**").permitAll()

                        // Catalog công khai
                        .pathMatchers(HttpMethod.GET, "/public/catalog/**").permitAll()

                        // Promotion public.
                        .pathMatchers(HttpMethod.GET, "/public/promotions/**").permitAll()

                        .pathMatchers(HttpMethod.POST, "/public/promotions/apply").permitAll()


                        // 2. ADMIN ENDPOINTS
                        // User profile + address.
                        .pathMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")

                        // 3) USER (đăng nhập)
                        // User profile, address, cart, order người dùng.
                        .pathMatchers("/users/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN")

                        // 4) INTERNAL
                        .pathMatchers("/internal/**").permitAll()

                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {})
                )

                .build();
    }
}
