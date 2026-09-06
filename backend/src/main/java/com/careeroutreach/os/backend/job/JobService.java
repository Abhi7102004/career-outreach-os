package com.careeroutreach.os.backend.job;

import com.careeroutreach.os.backend.company.Company;
import com.careeroutreach.os.backend.company.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
    }

    @Transactional
    public JobResponse create(JobCreateRequest request){
        var existing = jobRepository.findByUrl(request.url());
        if (existing.isPresent()) {
            return JobResponse.from(existing.get());
        }
        Company company = companyService.upsertByNormalizedName(request.companyName());
        if(company==null){
            throw new IllegalArgumentException(
                    "companyName normalized to nothing: '" + request.companyName() + "'");
        }

        Job job = new Job(request.url());
        job.setCompany(company);
        if (request.externalJobId() != null && !request.externalJobId().isBlank()) {
            job.setExternalJobId(request.externalJobId());
        }
        Job saved = jobRepository.save(job);
        log.info("Created job {} at company '{}' (externalId={})",
                saved.getId(), company.getName(), request.externalJobId());
        return JobResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listAll() {
        return jobRepository.findAll().stream()
                .map(JobResponse::from)
                .toList();
    }

}