package org.example.auth_service.service.impl;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
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
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Keycloak  keycloak;
    private final AuthEventProducer producer;
    private static final String DEFAULT_CUSTOMER_ROLE = "USER";

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.backend-client-id:backend}")
    private String backendClientId;

    @Override
    public String register(RegisterRequest request) {
        String userId = null;

        // Build thông itn user để tạo trong Keycloak
        UserRepresentation user = new UserRepresentation();
        // Nếu username trống, fallback sang email để tránh lỗi validate
        user.setUsername(resolveUsername(request));
        user.setEmail(request.getEmail());

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
            assignClientRole(userId, DEFAULT_CUSTOMER_ROLE);

            // Bắn event
            UserRegisteredEvent event = new UserRegisteredEvent();
            event.setUserId(userId);
            event.setUserName(user.getUsername());
            event.setEmail(user.getEmail());
            event.setFullName(request.getFullName());
            event.setPhone(request.getPhone());

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

    private void assignClientRole(String userId, String roleName) {
        // clientlevel role cần internal UUID của client (không dùng trực tiếp clientId "backend"
        List<ClientRepresentation> clients = keycloak.realm(realm).clients().findByClientId(backendClientId);

        if (clients.isEmpty()) {
            throw new ApplicationException("Cannot find client with id " + backendClientId);
        }

        String clientUuid = clients.get(0).getId();

        RoleRepresentation role = keycloak.realm(realm)
                .clients()
                .get(clientUuid)
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(userId)
                .roles()
                .clientLevel()
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
        MultivaluedMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultivaluedMap<String, String>> httpRequest = new HttpEntity<>(body, headers);

        // Dựng endpoint token từ config để không hard-code theo môi trường local.
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate
                    .postForEntity(tokenUrl, httpRequest, String.class);

            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApplicationException("Wrong username or password");
        }
    }
}
