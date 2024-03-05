package com.veridion.challenge.repository;

import com.veridion.challenge.domain.CompanyMerged;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CRUD operations on CompanyMerged entities.
 */
@Repository
public interface CompanyMergedRepository extends JpaRepository<CompanyMerged, Long> {
}
