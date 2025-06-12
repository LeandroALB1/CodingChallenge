package com.example.library.repository;

import com.example.library.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {

    @Query("SELECT b.isbn FROM Book b JOIN b.tags bt JOIN bt.tag t " +
            "WHERE t.name IN :tags GROUP BY b.isbn HAVING COUNT(DISTINCT t.name) = :size")
    List<String> findIsbnsByTags(@Param("tags") List<String> tags, @Param("size") long size);


    @Query(value = "SELECT tag_name, COUNT(*) FROM book_tags GROUP BY tag_name ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findTopUsedTags();

}
