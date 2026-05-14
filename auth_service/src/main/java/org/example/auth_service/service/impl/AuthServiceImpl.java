package org.example.auth_service.service.impl;

import org.example.auth_service.dto.request.CreateAdminRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.auth_service.dto.request.LoginRequest;
import org.example.auth_service.dto.request.RegisterRequest;
import org.example.auth_service.dto.response.TokenResponse;
import org.example.auth_service.exception.ApplicationException;
import org.example.auth_service.kafka.event.UserRegisteredEvent;
import org.example.auth_service.kafka.producer.AuthEventProducer;
import org.example.auth_service.service.AuthService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Keycloak  keycloak;
    private final AuthEventProducer producer;
    private static final String DEFAULT_CUSTOMER_ROLE = "CUSTOMER";
    private static final String ADMIN_ROLE = "ADMIN";

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.backend-client-id:auth-service}")
    private String backendClientId;

    @Override
    public String register(RegisterRequest request) {
        String userId = null;

        // Build thông itn user để tạo trong Keycloak
        UserRepresentation user = new UserRepresentation();
        // Nếu username trống, fallback sang email để tránh lỗi validate
        user.setUsername(resolveUsername(request));
        user.setEnabled(true);

        // Thiết lập mật khẩu ban đầu cho tài khoản
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(request.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        try {
            Response response = keycloak.realm(realm).users().create(user);
            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                log.error("Create user error: {}", errorMessage);
                throw new RuntimeException("Cannot create user in Keycloak");
            }

            // Keycloak trả về URL resource vừa tạo trong header Location
            String location = response.getHeaderString("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Cannot get created user location from Keycloak");
            }
            userId = location.substring(location.lastIndexOf("/") + 1);

            // Gán role khách hàng cho user mới
            assignRealmRole(userId, DEFAULT_CUSTOMER_ROLE);

            // Bắn event
            UserRegisteredEvent event = new UserRegisteredEvent();
            event.setUserId(userId);
            event.setUsername(user.getUsername());
            event.setEmail(request.getEmail());
            event.setFullName(request.getFullName());
            event.setPhone(request.getPhone());
            event.setRole(DEFAULT_CUSTOMER_ROLE);

            producer.publishUserRegistered(event);

            return userId;
        } catch (Exception e) {
            // Nếu lỗi xảy ra sau khi đã tạo user, rollback để tránh dữ liệu mồ côi
            if (userId != null) {
                keycloak.realm(realm).users().get(userId).remove();
                log.warn("Rolled back Keycloak user {}", userId);
            }
            throw e;
        }
    }

    @Override
    public String createAdmin(CreateAdminRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(request.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Cannot create admin in Keycloak");
        }

        String location = response.getHeaderString("Location");
        if (location == null || location.isBlank()) {
            throw new RuntimeException("Cannot get created admin location from Keycloak");
        }
        String userId = location.substring(location.lastIndexOf("/") + 1);
        assignRealmRole(userId, ADMIN_ROLE);

        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUserId(userId);
        event.setUsername(request.getUsername());
        event.setEmail(request.getEmail());
        event.setFullName(request.getFullName());
        event.setPhone(request.getPhone());
        event.setRole(ADMIN_ROLE);
        producer.publishUserRegistered(event);

        return userId;
    }

    private void assignRealmRole(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    private String resolveUsername(RegisterRequest request) {
        // Ưu tiên username người dùng nhập
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            return request.getUsername();
        }

        return request.getEmail();
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        // Password grant request dạng x-www-form-urlencoded theo chuẩn OIDC.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(body, headers);

        // Dựng endpoint token từ config để không hard-code theo môi trường local.
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<TokenResponse> response = restTemplate
                    .postForEntity(tokenUrl, httpRequest, TokenResponse.class);

            return response.getBody();
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            log.error("Login failed with status {} and body {}", e.getStatusCode(), responseBody);

            if (responseBody != null && responseBody.contains("Account is not fully set up")) {
                throw new ApplicationException("Account is not fully set up. Please verify required actions in Keycloak (e.g. verify email / update password).");
            }

            throw new ApplicationException("Wrong username or password");
        } catch (Exception e) {
            log.error("Login failed", e);
            throw new ApplicationException("Wrong username or password");
        }
    }
}
