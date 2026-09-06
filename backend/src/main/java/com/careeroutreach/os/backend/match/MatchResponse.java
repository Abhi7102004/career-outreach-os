package com.careeroutreach.os.backend.match;

import com.careeroutreach.os.backend.connection.Connection;

import java.time.LocalDate;
import java.util.List;

public record MatchResponse(
        Long connectionId,
        String firstName,
        String lastName,
        String profileUrl,
        String position,
        LocalDate connectedOn,
        int score,
        MatchTier tier,
        List<String> reasons
) {
    public MatchResponse {
        reasons = List.copyOf(reasons);
    }

    public static MatchResponse of(Connection c, ScoreResult sr) {
        return new MatchResponse(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getProfileUrl(),
                c.getPosition(),
                c.getConnectedOn(),
                sr.score(),
                sr.tier(),
                sr.reasons()
        );
    }
}