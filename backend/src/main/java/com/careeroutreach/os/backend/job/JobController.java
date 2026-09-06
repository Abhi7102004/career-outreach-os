package com.careeroutreach.os.backend.job;

import com.careeroutreach.os.backend.match.MatchService;
import com.careeroutreach.os.backend.match.MatchesResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final MatchService matchService;
    private final JobRepository jobRepository;
    public JobController(JobService jobService, MatchService matchService,JobRepository jobRepository) {
        this.jobService = jobService;
        this.matchService = matchService;
        this.jobRepository = jobRepository;
    }

    @PostMapping
    public ResponseEntity<MatchesResponse> create(
            @Valid @RequestBody JobCreateRequest request) {
        JobResponse created = jobService.create(request);
        MatchesResponse response = matchService.findMatchesForJob(created.id());
        return ResponseEntity
                .created(URI.create("/api/jobs/" + created.id()))
                .body(response);
    }

    @GetMapping
    public List<JobSummary> list() {
        return jobRepository.findAllWithConnectionCounts();
    }
}