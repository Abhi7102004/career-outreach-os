package com.careeroutreach.os.backend.match;

import com.careeroutreach.os.backend.company.Company;
import com.careeroutreach.os.backend.connection.Connection;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class MatchScorer {

    private static final int BASE_SAME_COMPANY = 100;

    private static final int RECENCY_WITHIN_1Y = 30;
    private static final int RECENCY_WITHIN_3Y = 15;
    private static final int RECENCY_WITHIN_5Y = 5;

    private static final int ROLE_ENGINEER_SENIOR = 50;
    private static final int ROLE_ENGINEER_MID = 48;
    private static final int ROLE_ENGINEER = 45;

    private static final int ROLE_TECH_LEAD = 45;
    private static final int ROLE_ENG_MANAGER = 30;
    private static final int ROLE_MANAGER_OTHER = 15;
    private static final int ROLE_DIRECTOR = 10;
    private static final int ROLE_VP = 5;

    private static final int ROLE_TECH_ADJACENT = 30;

    public ScoreResult score(Connection c, Company company) {

        int total = 0;
        List<String> reasons = new ArrayList<>();

        total += BASE_SAME_COMPANY;

        reasons.add(
                "Same company (" + company.getName() + "): +"
                        + BASE_SAME_COMPANY
        );

        int recencyPts = recencyPoints(c.getConnectedOn());

        if (recencyPts > 0) {
            total += recencyPts;

            reasons.add(
                    "Connected " + c.getConnectedOn() + ": +"
                            + recencyPts
            );
        }

        RolePoints rp = rolePoints(c.getPosition());

        if (rp.points() > 0) {
            total += rp.points();
            reasons.add("Role — " + rp.tier().getLabel()
                    + " (" + c.getPosition() + "): +" + rp.points());
        }

        return new ScoreResult(total,rp.tier(),reasons);
    }

    private int recencyPoints(LocalDate connectedOn) {

        if (connectedOn == null) {
            return 0;
        }

        long days = ChronoUnit.DAYS.between(
                connectedOn,
                LocalDate.now()
        );

        if (days < 0) {
            return 0;
        }

        if (days <= 365) {
            return RECENCY_WITHIN_1Y;
        }

        if (days <= 365 * 3) {
            return RECENCY_WITHIN_3Y;
        }

        if (days <= 365 * 5) {
            return RECENCY_WITHIN_5Y;
        }

        return 0;
    }

    private record RolePoints(int points, MatchTier tier) {
    }

    private static final RolePoints ZERO = new RolePoints(0, MatchTier.NONE);

    private RolePoints rolePoints(String position) {

        if (position == null || position.isBlank()) {
            return ZERO;
        }

        String p = normalizePosition(position);

        if (isInternOrTrainee(p)) {
            return ZERO;
        }

        if (isRecruitingOrHr(p)) {
            return ZERO;
        }

        if (isHiringManager(p)) {
            return ZERO;
        }

        if (isTechLead(p)) {
            return new RolePoints(ROLE_TECH_LEAD, MatchTier.TECH_LEAD);
        }

        if (isEngineeringManager(p)) {
            return new RolePoints(ROLE_ENG_MANAGER, MatchTier.ENG_MANAGER);
        }

        if (isTechnicalRole(p)) {
            if (isSeniorTechnicalRole(p)) {
                return new RolePoints(ROLE_ENGINEER_SENIOR, MatchTier.SENIOR_ENGINEER);
            }
            if (isMidLevelTechnicalRole(p)) {
                return new RolePoints(ROLE_ENGINEER_MID, MatchTier.MID_ENGINEER);
            }
            return new RolePoints(ROLE_ENGINEER, MatchTier.ENGINEER);

        }

        if (isTechnicalAdjacentRole(p)) {
            return new RolePoints(ROLE_TECH_ADJACENT, MatchTier.TECH_ADJACENT);
        }

        if (containsWord(p, "director")) {
            return new RolePoints(ROLE_DIRECTOR, MatchTier.DIRECTOR);
        }

        if (containsWord(p, "vp")
                || containsWord(p, "vice president")) {

            return new RolePoints(ROLE_VP, MatchTier.VP);
        }

        if (containsWord(p, "manager")) {
            return new RolePoints(ROLE_MANAGER_OTHER, MatchTier.MANAGER);
        }

        return ZERO;
    }

    private String normalizePosition(String position) {

        return position
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isInternOrTrainee(String p) {

        return containsWord(p, "intern")
                || containsWord(p, "internship")
                || containsWord(p, "trainee")
                || containsWord(p, "apprentice")
                || containsWord(p, "apprenticeship")
                || containsWord(p, "student")
                || containsWord(p, "summer school")
                || containsWord(p, "campus program")
                || containsWord(p, "graduate program");
    }

    private boolean isRecruitingOrHr(String p) {

        return containsWord(p, "recruiter")
                || containsWord(p, "recruiting")
                || containsWord(p, "talent acquisition")
                || containsWord(p, "talent partner")
                || containsWord(p, "talent sourcer")
                || containsWord(p, "sourcer")
                || containsWord(p, "sourcing")
                || containsWord(p, "human resources")
                || containsWord(p, "human resource")
                || containsWord(p, "people operations")
                || containsWord(p, "people partner")
                || containsWord(p, "hr");
    }

    private boolean isHiringManager(String p) {
        return containsWord(p, "hiring manager");
    }

    private boolean isTechLead(String p) {

        return containsWord(p, "tech lead")
                || containsWord(p, "technical lead");
    }

    private boolean isEngineeringManager(String p) {

        return containsWord(p, "engineering manager")
                || containsWord(p, "eng manager");
    }

    private boolean isTechnicalRole(String p) {

        return containsWord(p, "engineer")
                || containsWord(p, "developer")
                || containsWord(p, "programmer")

                || containsSdeOrSwe(p)

                || containsWord(p, "backend")
                || containsWord(p, "back end")
                || containsWord(p, "frontend")
                || containsWord(p, "front end")

                || containsWord(p, "full stack")
                || containsWord(p, "fullstack")

                || containsWord(p, "mobile engineer")
                || containsWord(p, "mobile developer")
                || containsWord(p, "android developer")
                || containsWord(p, "ios developer")

                || containsWord(p, "platform engineer")
                || containsWord(p, "infrastructure engineer")

                || containsWord(p, "site reliability")
                || containsWord(p, "sre")

                || containsWord(p, "devops")
                || containsWord(p, "cloud engineer")

                || containsWord(p, "data engineer")
                || containsWord(p, "analytics engineer")

                || containsWord(p, "ml engineer")
                || containsWord(p, "machine learning")
                || containsWord(p, "ai engineer")
                || containsWord(p, "artificial intelligence")

                || containsWord(p, "security engineer")
                || containsWord(p, "cybersecurity")

                || containsWord(p, "qa engineer")
                || containsWord(p, "test engineer")
                || containsWord(p, "automation engineer")

                || containsWord(p, "systems engineer")

                || containsWord(p, "software architect")
                || containsWord(p, "solutions engineer");
    }

    private boolean containsSdeOrSwe(String p) {

        return containsWord(p, "sde")
                || containsWord(p, "swe")
                || containsLevelSuffix(p, "sde")
                || containsLevelSuffix(p, "swe");
    }

    private boolean isTechnicalAdjacentRole(String p) {

        return containsWord(p, "applied scientist")
                || containsWord(p, "data scientist")
                || containsWord(p, "research scientist")
                || containsWord(p, "quality assurance")
                || containsWord(p, "qa")
                || containsWord(p, "test")
                || containsWord(p, "automation")
                || containsWord(p, "solutions architect")
                || containsWord(p, "technical program manager");
    }

    private boolean isSeniorTechnicalRole(String p) {

        return containsWord(p, "senior")
                || containsWord(p, "sr")
                || containsWord(p, "staff")
                || containsWord(p, "principal")
                || containsWord(p, "distinguished")
                || containsWord(p, "lead")
                || containsWord(p, "architect")

                || hasLevel(p, 3)
                || hasLevel(p, 4)

                || hasConcatenatedLevel(p, "sde", 3)
                || hasConcatenatedLevel(p, "sde", 4)
                || hasConcatenatedLevel(p, "swe", 3)
                || hasConcatenatedLevel(p, "swe", 4);
    }

    private boolean isMidLevelTechnicalRole(String p) {

        return hasLevel(p, 2)
                || hasConcatenatedLevel(p, "sde", 2)
                || hasConcatenatedLevel(p, "swe", 2)
                || containsWord(p, "mid level")
                || containsWord(p, "experienced engineer");
    }

    private boolean hasLevel(String p, int level) {

        String number = String.valueOf(level);
        String roman = romanLevel(level);

        return p.matches(
                ".*\\b(engineer|developer|sde|swe)\\s+"
                        + number
                        + "\\b.*"
        )
                || p.matches(
                ".*\\b(engineer|developer|sde|swe)\\s+"
                        + roman
                        + "\\b.*"
        );
    }

    private boolean hasConcatenatedLevel(
            String p,
            String role,
            int level
    ) {

        return p.matches(
                ".*\\b"
                        + role
                        + level
                        + "\\b.*"
        );
    }

    private boolean containsLevelSuffix(
            String p,
            String role
    ) {

        return p.matches(
                ".*\\b"
                        + role
                        + "[1-9]\\b.*"
        );
    }

    private String romanLevel(int level) {

        return switch (level) {
            case 1 -> "i";
            case 2 -> "ii";
            case 3 -> "iii";
            case 4 -> "iv";
            default -> "";
        };
    }

    private boolean containsWord(
            String text,
            String word
    ) {

        return (" " + text + " ")
                .contains(" " + word + " ");
    }
}