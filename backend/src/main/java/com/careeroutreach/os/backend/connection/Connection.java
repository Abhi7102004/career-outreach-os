package com.careeroutreach.os.backend.connection;

import com.careeroutreach.os.backend.company.Company;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "connections")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_url", unique = true, columnDefinition = "TEXT")
    private String profileUrl;

    @Column
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "company_raw")
    private String companyRaw;

    @Column
    private String position;

    @Column(name = "connected_on")
    private LocalDate connectedOn;      // date-only, no time

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Connection() {}

    public Connection(String firstName, String lastName, String profileUrl,
                      String email, String companyRaw, String position,
                      LocalDate connectedOn) {
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.profileUrl   = profileUrl;
        this.email        = email;
        this.companyRaw   = companyRaw;
        this.position     = position;
        this.connectedOn  = connectedOn;
        this.createdAt    = OffsetDateTime.now();
    }

    public Long getId()               { return id; }
    public String getFirstName()      { return firstName; }
    public String getLastName()       { return lastName; }
    public String getProfileUrl()     { return profileUrl; }
    public String getEmail()          { return email; }
    public Company getCompany()       { return company; }
    public String getCompanyRaw()     { return companyRaw; }
    public String getPosition()       { return position; }
    public LocalDate getConnectedOn() { return connectedOn; }

    public void setFirstName(String firstName)     { this.firstName = firstName; }
    public void setLastName(String lastName)       { this.lastName = lastName; }
    public void setEmail(String email)             { this.email = email; }
    public void setCompanyRaw(String companyRaw)   { this.companyRaw = companyRaw; }
    public void setPosition(String position)       { this.position = position; }
    public void setConnectedOn(LocalDate d)        { this.connectedOn = d; }
    public void setCompany(Company company) { this.company = company; }
}