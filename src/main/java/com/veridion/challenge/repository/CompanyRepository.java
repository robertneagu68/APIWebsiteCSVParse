package com.veridion.challenge.repository;

import com.veridion.challenge.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CRUD operations on Company entities.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
