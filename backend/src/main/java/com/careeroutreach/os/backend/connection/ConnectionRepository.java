package com.careeroutreach.os.backend.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByProfileUrl(String profileUrl);

    @Query("""
        SELECT c FROM Connection c
        WHERE c.company.id = :companyId
          AND c.profileUrl IS NOT NULL
        ORDER BY c.connectedOn DESC NULLS LAST
    """)
    List<Connection> findMatchesForCompany(@Param("companyId") Long companyId);
}