package com.careeroutreach.os.backend.company;

public record BackfillResult(
        long connectionsSeen,
        long alreadyLinked,
        long newlyLinked,
        long unlinkable,
        long companiesCreated
) {}