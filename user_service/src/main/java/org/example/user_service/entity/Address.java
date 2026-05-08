package org.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;import lombok.Data;

@Data
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "receiver_name")
    private String receiverName;

    private String phone;

    private String province;
    private String district;
    private String ward;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
