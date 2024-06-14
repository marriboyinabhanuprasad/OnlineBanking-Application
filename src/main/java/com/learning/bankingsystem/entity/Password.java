package com.learning.bankingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Password {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PasswordType type;

    @Column(name = "invalid_password_entry_count")
    private int invalidPasswordEntryCount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PasswordStatus status;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private Instant createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time", nullable = false)
    private Instant updatedTime;
}
