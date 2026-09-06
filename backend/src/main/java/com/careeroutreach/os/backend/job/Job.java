package com.careeroutreach.os.backend.job;

import com.careeroutreach.os.backend.company.Company;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;

    @Column(name = "external_job_id")
    private String externalJobId;

    @Column(length = 500)
    private String title;

    @Column(name = "raw_html", columnDefinition = "TEXT")
    private String rawHtml;

    @Column(name = "parsed_at")
    private OffsetDateTime parsedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Job() {}   // JPA needs a no-arg ctor

    public Job(String url) {
        this.url = url;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId()                 { return id; }
    public String getUrl()              { return url; }
    public String getTitle()            { return title; }
    public Company getCompany()         { return company; }
    public String getRawHtml()          { return rawHtml; }
    public OffsetDateTime getParsedAt() { return parsedAt; }
    public OffsetDateTime getCreatedAt(){ return createdAt; }

    public void setCompany(Company company)    { this.company = company; }

    public String getExternalJobId() { return externalJobId; }
    public void setExternalJobId(String externalJobId) {
        this.externalJobId = externalJobId;
    }
}
