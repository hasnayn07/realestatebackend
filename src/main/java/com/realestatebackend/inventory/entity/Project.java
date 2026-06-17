package com.realestatebackend.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;            // "Saffron City"

    @Column(length = 100)
    private String city;            // "Lahore"

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}