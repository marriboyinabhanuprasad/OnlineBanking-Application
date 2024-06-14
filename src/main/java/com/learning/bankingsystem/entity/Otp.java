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
@Entity(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @Column(name = "otp", nullable = false)
    private Long otp;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OtpStatus status;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OtpType type;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private Instant createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time", nullable = false)
    private Instant updatedTime;
}
