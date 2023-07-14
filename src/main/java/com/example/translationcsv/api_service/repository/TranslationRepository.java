package com.example.translationcsv.api_service.repository;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends PagingAndSortingRepository<TranslationEntity, Long>, JpaRepository<TranslationEntity, Long> {

}
