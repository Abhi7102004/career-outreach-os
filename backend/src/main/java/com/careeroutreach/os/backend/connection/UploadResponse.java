package com.careeroutreach.os.backend.connection;

public record UploadResponse(
        String filename,
        long sizeBytes,
        long recordsSeen,
        long created,
        long updated,
        long skipped
) {}