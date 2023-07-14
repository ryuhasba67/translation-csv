package com.example.translationcsv.api_service.dto;

import java.util.List;

public class BaseDto {

    protected String getDataAtIndex(List<String> row, int index) {
        if (index >= row.size()) {
            return null;
        } else {
            return row.get(index);
        }
    }
}
