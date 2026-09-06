package com.careeroutreach.os.backend.company;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class NameNormalizer {
    private static final Set<String> CORPORATE_SUFFIXES = Set.of(
            "inc", "llc", "ltd", "limited", "corp", "corporation",
            "co", "company", "gmbh", "ag", "plc", "pvt", "private",
            "sa", "srl", "bv", "nv", "kg", "kk"
    );

    public String normalize(String raw){
        if(raw==null) return null;

        String s = raw.toLowerCase(Locale.ROOT);
        s=s.replaceAll("[^a-z0-9\\s]"," ");
        s=s.replaceAll("\\s+", " ").trim();

        if (s.isEmpty()) return null;
        List<String> tokens = new ArrayList<>(List.of(s.split(" ")));

        while (!tokens.isEmpty()
                && CORPORATE_SUFFIXES.contains(tokens.get(tokens.size() - 1))) {
            tokens.remove(tokens.size() - 1);
        }

        if (tokens.isEmpty()) return null;
        return String.join(" ", tokens);
    }
}
