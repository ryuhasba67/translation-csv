package com.example.translationcsv.api_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LinksFileNameDto extends BaseDto {
    private Long sentenceId;
    private Long translationId;

    public LinksFileNameDto(List<String> row) {
        int i = 0;
        this.sentenceId = Long.valueOf(getDataAtIndex(row, i++));
        this.translationId = Long.valueOf(getDataAtIndex(row, i++));
    }
}
