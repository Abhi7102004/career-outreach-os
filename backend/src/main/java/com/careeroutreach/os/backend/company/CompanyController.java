package com.careeroutreach.os.backend.company;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController{
    private final CompanyService companyService;
    private final NameNormalizer nameNormalizer;

    public CompanyController(CompanyService companyService,NameNormalizer nameNormalizer){
        this.companyService=companyService;
        this.nameNormalizer=nameNormalizer;
    }

    @GetMapping("/normalise")
    public Map<String,String>normalize(@RequestParam String name){
        return Map.of(
                "raw", name,
                "normalized", String.valueOf(nameNormalizer.normalize(name))
        );
    }
    @PostMapping("/backfill")
    public BackfillResult backfill() {
        return companyService.backfillConnections();
    }
}