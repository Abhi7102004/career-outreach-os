package com.careeroutreach.os.backend.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job,Long>{
    Optional<Job> findByUrl(String url);
    boolean existsByUrl(String url);

    @Query(value = """
        SELECT
            j.id                AS id,
            j.url               AS url,
            j.external_job_id   AS externalJobId,
            j.company_id        AS companyId,
            co.name             AS companyName,
            COUNT(c.id)         AS connectionsAtCompany,
            j.created_at        AS createdAt
        FROM jobs j
        LEFT JOIN companies   co ON co.id = j.company_id
        LEFT JOIN connections c  ON c.company_id = j.company_id
                                 AND c.profile_url IS NOT NULL
        GROUP BY j.id, j.url, j.external_job_id, j.company_id, co.name, j.created_at
        ORDER BY j.created_at DESC
    """,nativeQuery = true)
    List<JobSummary>findAllWithConnectionCounts();
}