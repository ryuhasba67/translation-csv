package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.dto.LinksFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesAudioFileNameDto;
import com.example.translationcsv.api_service.dto.SentencesDto;
import com.example.translationcsv.api_service.entity.TranslationEntity;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {
    public static final String FILE_CHARSET = "UTF-8";


    public List<SentencesDto> readSentencesCsvFile(InputStream inputStream) {
        try {
            CsvListReader listReader = new CsvListReader(new InputStreamReader(inputStream, FILE_CHARSET), CsvPreference.TAB_PREFERENCE);
            List<SentencesDto> resultDtos = new ArrayList<>();

            List<String> row = listReader.read();

            while (row != null) {
                resultDtos.add(new SentencesDto(row));
                row = listReader.read();
            }

            return resultDtos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LinksFileNameDto> readLinksFileNameCsvFile(InputStream inputStream) {
        try {
            CsvListReader listReader = new CsvListReader(new InputStreamReader(inputStream, FILE_CHARSET), CsvPreference.TAB_PREFERENCE);
            List<LinksFileNameDto> resultDtos = new ArrayList<>();

            List<String> row = listReader.read();

            while (row != null) {
                resultDtos.add(new LinksFileNameDto(row));
                row = listReader.read();
            }

            return resultDtos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SentencesAudioFileNameDto> readSentencesAudioCsvFile(InputStream inputStream) {
        try {
            CsvListReader listReader = new CsvListReader(new InputStreamReader(inputStream, FILE_CHARSET), CsvPreference.TAB_PREFERENCE);
            List<SentencesAudioFileNameDto> resultDtos = new ArrayList<>();

            List<String> row = listReader.read();

            while (row != null) {
                resultDtos.add(new SentencesAudioFileNameDto(row));
                row = listReader.read();
            }

            return resultDtos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TranslationEntity> readTranslationCsvFile(InputStream inputStream) {
        try {
            CsvListReader listReader = new CsvListReader(new InputStreamReader(inputStream, FILE_CHARSET), CsvPreference.STANDARD_PREFERENCE);
            List<TranslationEntity> results = new ArrayList<>();

            List<String> row = listReader.read();
            while (row != null) {
                results.add(new TranslationEntity(row));
                row = listReader.read();
            }

            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}