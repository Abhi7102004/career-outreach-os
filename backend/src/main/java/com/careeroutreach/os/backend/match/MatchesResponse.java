package com.careeroutreach.os.backend.match;

import java.util.List;

public record MatchesResponse(
        Long jobId,
        String jobUrl,             // NEW
        String externalJobId,      // NEW — nullable
        Long companyId,
        String companyName,
        int totalMatches,
        List<MatchResponse> matches
) {}