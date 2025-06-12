package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.BookTag;
import com.example.library.entity.Tag;
import com.example.library.repository.BookRepository;
import com.example.library.repository.TagRepository;
import com.example.library.service.impl.LibraryServiceImpl;
import com.example.library.validation.IsbnValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @InjectMocks
    private LibraryServiceImpl service;

    @Mock
    private IsbnValidator validator;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private TagRepository tagRepo;

    private Book sampleBook;
    private Tag fantasyTag;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setIsbn("1234567890123");

        fantasyTag = new Tag();
        fantasyTag.setName("Fantasy");
    }

    @BeforeEach
    void setup() {
        sampleBook.setTags(new HashSet<>());
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepo.existsById("123")).thenReturn(true);
        service.delete("123");
        verify(bookRepo).deleteById("123");
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepo.existsById("404")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.delete("404"));
    }

    @Test
    void testRemoveTag_Success() {
        Tag sciFi = new Tag();
        sciFi.setName("SciFi");
        sampleBook.getTags().add(new BookTag(sampleBook, sciFi));
        when(bookRepo.findById("123")).thenReturn(Optional.of(sampleBook));

        service.removeTag("123", "SciFi");

        assertTrue(sampleBook.getTags().isEmpty());
    }

    @Test
    void testRemoveTag_BookNotFound() {
        when(bookRepo.findById("404")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.removeTag("404", "Tag"));
    }

    @Test
    void testListTags_Success() {
        sampleBook.getTags().add(new BookTag(sampleBook, fantasyTag));
        when(bookRepo.findById("123")).thenReturn(Optional.of(sampleBook));

        List<String> tags = service.listTags("123");

        assertEquals(List.of("Fantasy"), tags);
    }

    @Test
    void testListTags_BookNotFound() {
        when(bookRepo.findById("404")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.listTags("404"));
    }

    @Test
    void testListAll() {
        Book b1 = new Book();
        b1.setIsbn("111");
        Tag tag1 = new Tag();
        tag1.setName("A");
        b1.getTags().add(new BookTag(b1, tag1));

        Book b2 = new Book();
        b2.setIsbn("222");
        Tag tag2 = new Tag();
        tag2.setName("B");
        b1.getTags().add(new BookTag(b2, tag2));

        when(bookRepo.findAll()).thenReturn(List.of(b1, b2));

        Map<String, List<String>> result = service.listAll();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("111"));
        assertTrue(result.containsKey("222"));
    }

    @Test
    void testStats() {
        when(bookRepo.count()).thenReturn(2L);
        when(tagRepo.count()).thenReturn(5L);

        Object[] row = new Object[] {"Fantasy", 10L};
        when(tagRepo.findTopUsedTags()).thenReturn(Collections.singletonList((row)));

        Map<String, String> result = service.stats();

        assertEquals("2", result.get("books"));
        assertEquals("5", result.get("tags"));
        assertEquals("Fantasy (10x)", result.get("top-tag"));
    }
}