package com.example.translationcsv.api_service.controller;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import com.example.translationcsv.api_service.service.CsvService;
import com.example.translationcsv.api_service.service.ImportCsvToDbService;
import com.example.translationcsv.api_service.service.ParseAndMergeTranslationService;
import com.example.translationcsv.api_service.service.TranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/translations")
public class TranslationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationController.class);

    @Autowired
    private ParseAndMergeTranslationService parseAndMergeTranslationService;

    @Autowired
    private ImportCsvToDbService importCsvToDbService;

    @Autowired
    private TranslationService translationService;

    @GetMapping("/parse-and-merge-translation-to-csv-file")
    public String parseAndMergeTranslationToCsvFile() {
        return parseAndMergeTranslationService.parseAndMergeTranslationToCsvFile();
    }

    @GetMapping("/import-translation-to-db")
    public boolean importTranslationToDb() {
        try {
            return importCsvToDbService.importTranslationToDb();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
    }

    @GetMapping("/get-translation-page")
    public Page<TranslationEntity> getTranslationPages(@RequestParam int page,
                                                       @RequestParam int pageSize) {
        return translationService.getTranslationPages(page, pageSize);
    }
}
