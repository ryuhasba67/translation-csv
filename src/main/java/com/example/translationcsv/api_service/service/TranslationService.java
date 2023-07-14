package com.example.translationcsv.api_service.service;

import com.example.translationcsv.api_service.entity.TranslationEntity;
import com.example.translationcsv.api_service.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TranslationService {

    @Autowired
    private TranslationRepository repository;

    public Page<TranslationEntity> getTranslationPages(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return repository.findAll(pageable);
    }
}
