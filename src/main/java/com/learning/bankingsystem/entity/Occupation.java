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
public class Occupation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "user_uuid")
    private User user;

    @Column(name = "occupation_type", nullable = false)
    private String occupationType;

    @Column(name = "source_income", nullable = false)
    private String sourceIncome;

    @Column(name = "gross_annual_income", nullable = false)
    private String grossAnnualIncome;

    @Column(name = "created_time", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdTime;

    @Column(name = "updated_time", nullable = false)
    @UpdateTimestamp
    private Instant updatedTime;
}
