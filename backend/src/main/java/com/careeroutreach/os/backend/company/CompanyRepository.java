package com.careeroutreach.os.backend.company;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long>{

    Optional<Company> findByNormalizedName(String normalizedName);
}