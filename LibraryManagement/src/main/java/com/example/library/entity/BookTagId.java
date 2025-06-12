package com.example.library.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class BookTagId implements Serializable {
    private String isbn;
    private String tagName;
    public BookTagId() {}
    public BookTagId(String isbn, String tagName) {
        this.isbn = isbn;
        this.tagName = tagName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookTagId)) return false;
        BookTagId that = (BookTagId) o;
        return Objects.equals(isbn, that.isbn) && Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, tagName);
    }
}