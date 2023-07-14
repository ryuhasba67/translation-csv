package com.example.translationcsv.api_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SentencesDto extends BaseDto {

    private Long sentenceId;
    private String languageCode;
    private String text;


    public SentencesDto(List<String> row) {
        int i = 0;
        this.sentenceId = Long.valueOf(getDataAtIndex(row, i++));
        this.languageCode = getDataAtIndex(row, i++);
        this.text = getDataAtIndex(row, i++);
    }
}
