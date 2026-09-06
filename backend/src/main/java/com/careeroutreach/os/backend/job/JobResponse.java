package com.careeroutreach.os.backend.job;

import java.time.OffsetDateTime;

public record JobResponse(
        Long id,
        String url,
        String externalJobId,
        Long companyId,
        String companyName,
        OffsetDateTime createdAt
) {
    public static JobResponse from(Job job) {
        return new JobResponse(
                job.getId(),
                job.getUrl(),
                job.getExternalJobId(),
                job.getCompany() != null ? job.getCompany().getId()   : null,
                job.getCompany() != null ? job.getCompany().getName() : null,
                job.getCreatedAt()
        );
    }
}