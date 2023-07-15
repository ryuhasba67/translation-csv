package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.dto.LinksFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesAudioFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesDto;
import com.example.translationcsv.api_service.entity.TranslationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.Optional;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParseAndMergeTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvService.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private CsvService csvService;

    @Value("${application.translation-file-path}")
    private String translationFilePath;

    public String parseAndMergeTranslationToCsvFile() {
        try {
            Resource sentencesResource = resourceLoader.getResource("classpath:/static/sentences.csv");
            File sentencesCsvFile = sentencesResource.getFile();
            Resource linksFileNameResource = resourceLoader.getResource("classpath:/static/links.csv");
            File linksFileNameCsvFile = linksFileNameResource.getFile();
            Resource sentencesAudioResource = resourceLoader.getResource("classpath:/static/sentences_with_audio.csv");
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

        Set<Long> engSentenceIds = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("eng"))
                .map(SentencesDto::getSentenceId)
                .collect(Collectors.toSet());

        Map<Long, String> mapEngSentenceBySentenceId = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("eng"))
                .collect(Collectors.toMap(SentencesDto::getSentenceId, SentencesDto::getText));

        Set<Long> vieSentenceIds = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("vie"))
                .map(SentencesDto::getSentenceId)
                .collect(Collectors.toSet());

        Map<Long, String> mapVieSentenceBySentenceId = listSentenceFromFile
                .stream()
                .filter(sentencesDto -> sentencesDto.getLanguageCode().equals("vie"))
                .collect(Collectors.toMap(SentencesDto::getSentenceId, SentencesDto::getText));

        Set<LinksFileNameDto> listFilter = listLinkFileNameFromFile
                .stream()
                .filter(linksFileNameDto -> engSentenceIds.contains(linksFileNameDto.getSentenceId())
                        && vieSentenceIds.contains(linksFileNameDto.getTranslationId()))
                .collect(Collectors.toSet());

        Map<Long, Long> mapTranslationIdBySentenceId = listFilter
                .stream()
                .collect(Collectors.toMap(LinksFileNameDto::getSentenceId, LinksFileNameDto::getTranslationId, (existing, replacement) -> existing));

        Map<Long, String> mapAudioUrlBySentenceId = listSentenceAudioFile
                .stream()
                .filter(sentencesAudioFileNameDto ->  engSentenceIds.contains(sentencesAudioFileNameDto.getSentenceId())
                        && sentencesAudioFileNameDto.getAttributionUrl() != null)
                .collect(Collectors.toMap(SentencesAudioFileNameDto::getSentenceId, SentencesAudioFileNameDto::getAttributionUrl, (existing, replacement) -> existing));

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
        ICsvBeanWriter beanWriter;
        final String[] header = new String[]{"EngSentenceId", "EngSentenceText", "EngSentenceAudioUrl", "VieSentenceId", "VieSentenceText"};
        File translationFile;
        try {
            Path translationPath = Path.of(translationFilePath);
            Files.deleteIfExists(translationPath);
            translationFile = new File(translationPath.toUri());
            beanWriter = new CsvBeanWriter(new FileWriter(translationFile), CsvPreference.STANDARD_PREFERENCE);
            CellProcessor[] processors = new CellProcessor[]{
                    new NotNull(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
            };
            for (TranslationEntity translation : translationData) {
                beanWriter.write(translation, header, processors);
            }
            beanWriter.close();
        } catch (IOException ioException) {
            LOGGER.error(ioException.getMessage());
            return null;
        }
        return translationFile.getPath();
    }
}
