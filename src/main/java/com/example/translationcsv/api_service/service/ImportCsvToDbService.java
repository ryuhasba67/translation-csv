package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import com.example.translationcsv.api_service.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ImportCsvToDbService {

    @Autowired
    private TranslationRepository repository;

    @Autowired
    private CsvService csvService;

    public boolean importTranslationToDb() throws IOException {
        boolean isTranslationFileExist = new ClassPathResource("translation.csv").exists();
        if (isTranslationFileExist) {
            File translationFile = new ClassPathResource("translation.csv").getFile();
            List<TranslationEntity> translationsData = csvService.readTranslationCsvFile(new FileInputStream(translationFile));
            repository.saveAll(translationsData);
            return true;
        }
        return false;
    }
}
