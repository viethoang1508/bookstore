package org.example.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    private String id; // lấy từ Keycloak

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    private String username;

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
