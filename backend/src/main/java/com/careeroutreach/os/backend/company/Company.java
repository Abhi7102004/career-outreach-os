package com.careeroutreach.os.backend.company;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(name="normalized_name",nullable = false,unique = true)
    private String normalizedName;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Company() {}

    public Company(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getNormalizedName() { return normalizedName; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
