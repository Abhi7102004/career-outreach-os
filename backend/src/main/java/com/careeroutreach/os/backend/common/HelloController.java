package com.careeroutreach.os.backend.common;

import com.careeroutreach.os.backend.company.CompanyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class HelloController {
    private final CompanyRepository companyRepository;

    public HelloController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "status", "ok",
                "companies_in_db", companyRepository.count(),
                "timestamp", OffsetDateTime.now()
        );
    }
}