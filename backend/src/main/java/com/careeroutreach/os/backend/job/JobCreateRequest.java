package com.careeroutreach.os.backend.job;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record JobCreateRequest(
        @NotBlank(message = "url must not be blank")
        @URL(message = "url must be a valid URL")
        String url,

        @NotBlank(message = "companyName must not be blank")
        String companyName,

        String externalJobId
) {}