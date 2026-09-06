package com.careeroutreach.os.backend.match;

import com.careeroutreach.os.backend.company.Company;
import com.careeroutreach.os.backend.connection.Connection;
import com.careeroutreach.os.backend.connection.ConnectionRepository;
import com.careeroutreach.os.backend.job.Job;
import com.careeroutreach.os.backend.job.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class MatchService {
    private final JobRepository jobRepository;
    private final ConnectionRepository connectionRepository;
    private final MatchScorer matchScorer;

    public MatchService(JobRepository jobRepository,
                        ConnectionRepository connectionRepository,
                        MatchScorer matchScorer) {
        this.jobRepository = jobRepository;
        this.connectionRepository = connectionRepository;
        this.matchScorer = matchScorer;
    }

    @Transactional(readOnly = true)
    public MatchesResponse findMatchesForJob(Long jobId){
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Job not found: " + jobId));

        Company company = job.getCompany();
        if (company == null) {
            return new MatchesResponse(
                    jobId, job.getUrl(), job.getExternalJobId(),
                    null, null, 0, List.of()
            );
        }
        List<Connection> hits = connectionRepository.findMatchesForCompany(company.getId());
        List<MatchResponse> matches = hits.stream()
                // your Day 7 filters — retained
                .filter(c -> c.getPosition() != null)
                .filter(c -> {
                    String position = c.getPosition().toLowerCase(Locale.ROOT);
                    return !position.contains("intern") && !position.contains("school");
                })
                // score every remaining match
                .map(c -> MatchResponse.of(c, matchScorer.score(c, company)))
                // score desc, tiebreak by most-recent connection
                .sorted(
                        Comparator.comparingInt(MatchResponse::score).reversed()
                                .thenComparing(
                                        MatchResponse::connectedOn,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                )
                .toList();

        return new MatchesResponse(
                jobId,
                job.getUrl(),
                job.getExternalJobId(),
                company.getId(),
                company.getName(),
                matches.size(),
                matches
        );
    }
}
