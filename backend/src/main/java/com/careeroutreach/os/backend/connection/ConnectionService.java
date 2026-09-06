package com.careeroutreach.os.backend.connection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ConnectionService {

    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);
    private static final DateTimeFormatter LI_DATE =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    private static final String HEADER_PREFIX = "First Name";

    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Transactional
    public UploadResponse importCsv(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        String csvPayload = stripPreamble(file);
        long created = 0, updated = 0, skipped = 0, seen = 0;

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setIgnoreHeaderCase(true)
                .build();

        try ( StringReader sr = new StringReader(csvPayload);
              CSVParser parser = new CSVParser(sr,format)){

            for(CSVRecord record : parser){
                seen++;
                try{
                    String profileUrl = get(record,"URL");
                    if(profileUrl==null){
                        skipped++;
                        continue;
                    }
                    String firstName    = get(record, "First Name");
                    String lastName     = get(record, "Last Name");
                    String email        = get(record, "Email Address");
                    String companyRaw   = get(record, "Company");
                    String position     = get(record, "Position");
                    LocalDate connected = parseDate(get(record, "Connected On"));

                    Optional<Connection> existing = connectionRepository.findByProfileUrl(profileUrl);

                    if(existing.isPresent()){
                        Connection c = existing.get();
                        c.setFirstName(firstName);
                        c.setLastName(lastName);
                        c.setEmail(email);
                        c.setCompanyRaw(companyRaw);
                        c.setPosition(position);
                        c.setConnectedOn(connected);
                        connectionRepository.save(c);
                        updated++;
                    }
                    else{
                        connectionRepository.save(new Connection(
                                firstName, lastName, profileUrl,
                                email, companyRaw, position, connected
                        ));
                        created++;
                    }
                }
                catch (Exception e) {
                    log.warn("Skipping row {}: {}", seen, e.getMessage());
                    skipped++;
                }
            }
        }

        return new UploadResponse(
                file.getOriginalFilename(),
                file.getSize(),
                seen,created,updated,skipped
        );
    }

    public String stripPreamble(MultipartFile file) throws IOException {
        List<String> lines=new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8))){
            String line;
            while ((line=reader.readLine())!=null){
                lines.add(line);
            }
        }

        int headerIdx = -1;
        for(int i=0;i<lines.size();i++){
            if(lines.get(i).startsWith(HEADER_PREFIX)){
                headerIdx=i;
                break;
            }
        }
        if (headerIdx == -1) {
            throw new IllegalArgumentException(
                    "Could not find header row (expected a line starting with '"
                            + HEADER_PREFIX + "'). Is this really a LinkedIn Connections export?");
        }
        return String.join("\n", lines.subList(headerIdx, lines.size()));
    }

    private String get(CSVRecord record,String column){
        if (!record.isMapped(column)) return null;
        String v = record.get(column);
        if(v==null) return null;
        String t=v.trim();
        return t.isEmpty() ? null:t;
    }

    private LocalDate parseDate(String s){
        if(s==null) return null;
        try {
            return LocalDate.parse(s, LI_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}