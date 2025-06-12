package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "book_tags", indexes = @Index(columnList = "tag_name"))
public class BookTag {
    @EmbeddedId
    private BookTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("isbn")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagName")
    private Tag tag;

    public BookTag() {}
    public BookTag(Book book, Tag tag) {
        this.book = book;
        this.tag = tag;
        this.id = new BookTagId(book.getIsbn(), tag.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookTag)) return false;
        BookTag bookTag = (BookTag) o;
        return Objects.equals(id, bookTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
