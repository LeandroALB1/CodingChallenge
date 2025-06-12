package com.example.library.service.impl;

import jakarta.transaction.Transactional;
import com.example.library.entity.Book;
import com.example.library.entity.BookTag;
import com.example.library.entity.Tag;
import com.example.library.repository.BookRepository;
import com.example.library.repository.TagRepository;
import com.example.library.service.LibraryService;
import com.example.library.validation.IsbnValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryServiceImpl implements LibraryService{
    @Autowired
    private IsbnValidator validator;
    @Autowired private BookRepository bookRepo;
    @Autowired private TagRepository tagRepo;


    @Transactional
    public List<String> search(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("Error, no tags provided.");
        }
        return tagRepo.findIsbnsByTags(tags, tags.size());
    }

    @Transactional
    public String store(String isbn, List<String> tagNames) {
        validator.validate(isbn);
        if (tagNames == null || tagNames.isEmpty()) {
            throw new IllegalArgumentException("Error, no tags provided.");
        }
        Book book = bookRepo.findById(isbn).orElseGet(() -> {
            Book b = new Book(); b.setIsbn(isbn); return b;
        });
        book.getTags().clear();
        var uniqueNames = new HashSet<>(tagNames);
        for (String name : uniqueNames) {
            Tag tag = tagRepo.findById(name).orElse(null);
            if (tag == null) {
                tag = new Tag();
                tag.setName(name);
                tag = tagRepo.save(tag);
            }
            book.getTags().add(new BookTag(book, tag));
        }
        bookRepo.save(book);
        return "Ok";
    }

    @Transactional
    @Override
    public String delete(String isbn) {
        if (!bookRepo.existsById(isbn)) {
            throw new IllegalArgumentException("Error, book not found.");
        }
        bookRepo.deleteById(isbn);
        return "Ok";
    }

    @Transactional
    @Override
    public String removeTag(String isbn, String tagName) {
        Book book = bookRepo.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Error, book not found."));
        boolean removed = book.getTags().removeIf(bt -> bt.getTag().getName().equals(tagName));
        if (!removed) {
            throw new IllegalArgumentException("Error, tag not found on this book.");
        }
        bookRepo.save(book);
        return "Ok";
    }

    @Transactional
    @Override
    public List<String> listTags(String isbn) {
        Book book = bookRepo.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Error, book not found."));
        return book.getTags().stream()
                .map(bt -> bt.getTag().getName())
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Map<String, List<String>> listAll() {
        return bookRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Book::getIsbn,
                        b -> b.getTags().stream()
                                .map(bt -> bt.getTag().getName())
                                .sorted()
                                .collect(Collectors.toList())
                ));
    }

    @Override
    public Map<String, String> stats() {
        long bookCount = bookRepo.count();
        long tagCount = tagRepo.count();

        String topTag = "N/A";
        List<Object[]> topTags = tagRepo.findTopUsedTags();
        if (!topTags.isEmpty()) {
            Object[] row = topTags.get(0);
            String tagName = (String) row[0];
            Number usageCount = (Number) row[1];
            topTag = tagName + " (" + usageCount + "x)";
        }

        Map<String, String> result = new HashMap<>();
        result.put("books", String.valueOf(bookCount));
        result.put("tags", String.valueOf(tagCount));
        result.put("top-tag", topTag);
        return result;
    }

}
