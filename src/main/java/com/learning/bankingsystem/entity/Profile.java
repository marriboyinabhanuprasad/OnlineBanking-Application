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
public class Profile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileStatus profileStatus;

    @Column(name = "admin_action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminActionType adminActionType;

    @Column(name = "admin_comments")
    private String comments;

    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private Instant createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time", nullable = false)
    private Instant updatedTime;
}
