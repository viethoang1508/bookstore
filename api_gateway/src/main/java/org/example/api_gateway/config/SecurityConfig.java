package org.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
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
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )

                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
            Collection<GrantedAuthority> roleAuthorities = extractRealmRoles(jwt);

            return Stream.concat(scopeAuthorities.stream(), roleAuthorities.stream())
                    .collect(Collectors.toSet());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }
        Object roles = realmAccess.get("roles");
        if (!(roles instanceof Collection<?> roleCollection)) {
            return List.of();
        }
        return roleCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
