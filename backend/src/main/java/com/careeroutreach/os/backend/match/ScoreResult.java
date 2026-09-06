package com.careeroutreach.os.backend.match;

import java.util.List;

public record ScoreResult(
        int score,
        MatchTier tier,
        List<String> reasons
) {
    public ScoreResult {
        reasons = List.copyOf(reasons);
    }
}