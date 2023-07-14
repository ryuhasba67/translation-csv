package com.example.translationcsv.api_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "translation")
@NoArgsConstructor
@AllArgsConstructor
public class TranslationEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long engSentenceId;
    private String engSentenceText;
    private String engSentenceAudioUrl;
    private Long vieSentenceId;
    private String vieSentenceText;


    public TranslationEntity(List<String> row) {
        int i = 0;
        this.engSentenceId = Long.valueOf(getDataAtIndex(row, i++));
        this.engSentenceText = getDataAtIndex(row, i++);
        this.engSentenceAudioUrl = getDataAtIndex(row, i++);
        this.vieSentenceId = Long.valueOf(getDataAtIndex(row, i++));
        this.vieSentenceText = getDataAtIndex(row, i++);
    }

    private String getDataAtIndex(List<String> row, int index) {
        if (index >= row.size()) {
            return null;
        } else {
            return row.get(index);
        }
    }
}
