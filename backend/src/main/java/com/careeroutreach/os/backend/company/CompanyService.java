package com.careeroutreach.os.backend.company;

import com.careeroutreach.os.backend.connection.Connection;
import com.careeroutreach.os.backend.connection.ConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ConnectionRepository connectionRepository;
    private final NameNormalizer nameNormalizer;

    public CompanyService(CompanyRepository companyRepository,
                          ConnectionRepository connectionRepository,
                          NameNormalizer nameNormalizer) {
        this.companyRepository = companyRepository;
        this.connectionRepository = connectionRepository;
        this.nameNormalizer = nameNormalizer;
    }

    public Company upsertByNormalizedName(String rawName) {
        String normalized = nameNormalizer.normalize(rawName);
        if (normalized == null) return null;
        return companyRepository.findByNormalizedName(normalized)
                .orElseGet(() -> companyRepository.save(new Company(rawName, normalized)));
    }

    @Transactional
    public BackfillResult backfillConnections() {
        List<Connection> connections = connectionRepository.findAll();
        long companiesBefore=connections.size();
        long alreadyLinked = 0, newlyLinked = 0, unlinkable = 0;

        for (Connection c : connections) {
            if (c.getCompany() != null) {
                alreadyLinked++;
                continue;
            }
            if (c.getCompanyRaw() == null || c.getCompanyRaw().isBlank()) {
                unlinkable++;
                continue;
            }

            Company company = upsertByNormalizedName(c.getCompanyRaw());
            if (company == null) {
                unlinkable++;
                continue;
            }
            c.setCompany(company);
            connectionRepository.save(c);
            newlyLinked++;
        }
        long companiesCreated = companiesBefore-companyRepository.count();
        return new BackfillResult(
                connections.size(),
                alreadyLinked,
                newlyLinked,
                unlinkable,
                companiesCreated
        );
    }
}