package org.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /*
    Cấu hình security cho API Gateway theo mô hình JWT
    Nguyên tắc:
    - Disable CSRF vì hệ thống đang dùng JWT stateless qua Authorization header
    - Khai báo rõ endpoint public/private theo từng service
    - Các endpoint quản trị yêu cầu ROLE_ADMIN
    - Mọi endpoint chưa liệt kê sẽ yêu cầu authenticate để tránh hở quyền
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                //api gateway dùng JWT -> không cần CSRF token cho request stateful
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchanges
                        // 1) PUBLIC ENDPOINTS
                        // Auth service: đăng ký / đăng nhập.
                        .pathMatchers("/public/auth/**").permitAll()

                        // Catalog (book service) cho client public.
                        .pathMatchers(HttpMethod.GET, "/public/catalog/**").permitAll()

                        // Promotion public.
                        .pathMatchers(HttpMethod.GET, "/public/promotions/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/public/promotions/apply").permitAll()


                        // 2. USER/ADMIN ENDPOINTS
                        // User profile + address.
                        .pathMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/addresses/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Cart service.
                        .pathMatchers("/user/cart/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Order service - luồng người dùng.
                        .pathMatchers("/user/orders/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                )
    }
}
