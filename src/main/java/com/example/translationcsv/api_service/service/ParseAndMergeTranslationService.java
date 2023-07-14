package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.dto.LinksFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesAudioFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesDto;
import com.example.translationcsv.api_service.entity.TranslationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParseAndMergeTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvService.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private CsvService csvService;

    public String parseAndMergeTranslationToCsvFile() {
        try {
            Resource sentencesResource = resourceLoader.getResource("classpath:sentences.csv");
            File sentencesCsvFile = sentencesResource.getFile();
            Resource linksFileNameResource = resourceLoader.getResource("classpath:links.csv");
            File linksFileNameCsvFile = linksFileNameResource.getFile();
            Resource sentencesAudioResource = resourceLoader.getResource("classpath:sentences_with_audio.csv");
            File sentencesAudioCsvFile = sentencesAudioResource.getFile();

            List<SentencesDto> listSentenceFromFile = csvService.readSentencesCsvFile(new FileInputStream(sentencesCsvFile.getAbsolutePath()));
            List<LinksFileNameDto> listLinkFileNameFromFile = csvService.readLinksFileNameCsvFile(new FileInputStream(linksFileNameCsvFile.getAbsolutePath()));
            List<SentencesAudioFileNameDto> listSentenceAudioFile = csvService.readSentencesAudioCsvFile(new FileInputStream(sentencesAudioCsvFile.getAbsolutePath()));


            List<TranslationEntity> translationData = processTranslationData(listSentenceFromFile, listLinkFileNameFromFile, listSentenceAudioFile);

            return convertTranslationDataToCsvFile(translationData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TranslationEntity> processTranslationData(List<SentencesDto> listSentenceFromFile,
                                                           List<LinksFileNameDto> listLinkFileNameFromFile,
                                                           List<SentencesAudioFileNameDto> listSentenceAudioFile) {
        List<TranslationEntity> translationResults = new ArrayList<>();

        List<Long> engSentenceIds = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("eng"))
                .map(SentencesDto::getSentenceId)
                .collect(Collectors.toList());

        Map<Long, String> mapEngSentenceBySentenceId = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("eng"))
                .collect(Collectors.toMap(SentencesDto::getSentenceId, SentencesDto::getText));

        List<Long> vieSentenceIds = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("vie"))
                .map(SentencesDto::getSentenceId)
                .collect(Collectors.toList());

        Map<Long, String> mapVieSentenceBySentenceId = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("vie"))
                .collect(Collectors.toMap(SentencesDto::getSentenceId, SentencesDto::getText));

        Map<Long, Long> mapTranslationIdBySentenceId = listLinkFileNameFromFile
                .stream()
                .filter(linksFileNameDto -> engSentenceIds.contains(linksFileNameDto.getSentenceId()))
                .filter(linksFileNameDto -> vieSentenceIds.contains(linksFileNameDto.getTranslationId()))
                .collect(Collectors.toMap(LinksFileNameDto::getSentenceId, LinksFileNameDto::getTranslationId));

        Map<Long, String> mapAudioUrlBySentenceId = listSentenceAudioFile
                .stream()
                .collect(Collectors.toMap(SentencesAudioFileNameDto::getSentenceId, SentencesAudioFileNameDto::getAttributionUrl));

        engSentenceIds.forEach(engSentenceId -> {
            TranslationEntity translation = new TranslationEntity();
            translation.setEngSentenceId(engSentenceId);

            if (mapEngSentenceBySentenceId.get(engSentenceId) != null) {
                translation.setEngSentenceText(mapEngSentenceBySentenceId.get(engSentenceId));
            }

            if (mapAudioUrlBySentenceId.get(engSentenceId) != null) {
                translation.setEngSentenceAudioUrl(mapAudioUrlBySentenceId.get(engSentenceId));
            }

            if (mapTranslationIdBySentenceId.get(engSentenceId) != null) {
                Long vieSentenceId = mapTranslationIdBySentenceId.get(engSentenceId);
                translation.setVieSentenceId(vieSentenceId);

                if (mapVieSentenceBySentenceId.get(vieSentenceId) != null) {
                    translation.setVieSentenceText(mapVieSentenceBySentenceId.get(vieSentenceId));
                }
            }
            translationResults.add(translation);
        });

        return translationResults;
    }

    private String convertTranslationDataToCsvFile(List<TranslationEntity> translationData) {
        ICsvBeanWriter beanWriter = null;
        final String[] header = new String[]{"EngSentenceId", "EngSentenceText", "EngSentenceAudioUrl", "VieSentenceId", "VieSentenceText"};
        File translationFile;
        try {
            translationFile = new ClassPathResource(
                    "translation.csv").getFile();
            beanWriter = new CsvBeanWriter(new FileWriter(translationFile), CsvPreference.STANDARD_PREFERENCE);
            CellProcessor[] processors = new CellProcessor[]{
                    new ParseLong(),
                    new NotNull(),
                    new NotNull(),
                    new ParseLong(),
                    new NotNull(),
            };
            for (TranslationEntity translation : translationData) {
                beanWriter.write(translation, header, processors);
            }
        } catch (IOException ioException) {
            LOGGER.error(ioException.getMessage());
            return null;
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return translationFile.getPath();
    }
}
