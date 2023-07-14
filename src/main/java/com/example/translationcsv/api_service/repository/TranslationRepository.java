package com.example.translationcsv.api_service.repository;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends PagingAndSortingRepository<TranslationEntity, Long>, JpaRepository<TranslationEntity, Long> {

    // "COPY translation FROM '"+ translationFile.getPath() +"' WITH (FORMAT csv);";
    @Query(value = "COPY 'translation' FROM 'D:\\H_Rabiloo\\translation-csv\\translation-csv\\src\\main\\resources\\translation.csv' DELIMITER ',' CSV ENCODING 'UTF8';",
            nativeQuery = true)
    void loadTranslationFileIntoTable();
}
