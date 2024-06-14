package com.learning.bankingsystem.repository;

import com.learning.bankingsystem.entity.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OccupationRepository extends JpaRepository<Occupation, UUID> {
}
