package com.careeroutreach.os.backend.match;

/**
 * The seniority/role tier of a matched connection.
 * The enum name is the API-facing stable key; the label is for display.
 */
public enum MatchTier {
    SENIOR_ENGINEER("Senior Engineer"),
    MID_ENGINEER("Mid-Level Engineer"),
    ENGINEER("Engineer"),
    TECH_LEAD("Tech Lead"),
    ENG_MANAGER("Engineering Manager"),
    TECH_ADJACENT("Technical-Adjacent"),
    DIRECTOR("Director"),
    VP("VP"),
    MANAGER("Manager"),
    NONE("");

    private final String label;

    MatchTier(String label) { this.label = label; }
    public String getLabel() { return label; }
}