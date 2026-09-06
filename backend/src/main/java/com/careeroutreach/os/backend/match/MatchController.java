package com.careeroutreach.os.backend.match;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/{id}/matches")
    public MatchesResponse getMatches(@PathVariable Long id) {
        return matchService.findMatchesForJob(id);
    }
}
