package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import com.example.translationcsv.api_service.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class ImportCsvToDbService {

    @Autowired
    private TranslationRepository repository;

    @Autowired
    private CsvService csvService;

    @Value("${application.translation-file-path}")
    private String translationFilePath;

    @Transactional
    public boolean importTranslationToDb() throws IOException {
        Path translationPath = Path.of(translationFilePath);
        File translationFile = new File(translationPath.toUri());
        if (translationFile.exists()) {
            List<TranslationEntity> translationsData = csvService.readTranslationCsvFile(new FileInputStream(translationFile));
            repository.saveAll(translationsData);
            return true;
        }
        return false;
    }
}
