package com.careeroutreach.os.backend.job;
import java.time.Instant;

public interface JobSummary {
    Long getId();
    String getUrl();
    String getExternalJobId();
    Long getCompanyId();
    String getCompanyName();
    Long getConnectionsAtCompany();
    Instant getCreatedAt();
}
