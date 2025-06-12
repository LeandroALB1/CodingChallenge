package com.example.library.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface LibraryService {
    List<String> search(List<String> tags);

    String store(String isbn, List<String> tags);

    @Transactional
    String delete(String isbn);

    @Transactional
    String removeTag(String isbn, String tagName);

    @Transactional
    List<String> listTags(String isbn);

    @Transactional
    Map<String, List<String>> listAll();

    @Transactional
    Map<String, String> stats();
}
