package com.example.translationcsv.api_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SentencesAudioFileNameDto extends BaseDto {

    private Long sentenceId;
    private String username;
    private String license;
    private String attributionUrl;

    public SentencesAudioFileNameDto(List<String> row) {
        int i = 0;
        this.sentenceId = Long.valueOf(getDataAtIndex(row, i++));
        this.username = getDataAtIndex(row, i++);
        this.license = getDataAtIndex(row, i++);
        this.attributionUrl = getDataAtIndex(row, i++);
    }
}
