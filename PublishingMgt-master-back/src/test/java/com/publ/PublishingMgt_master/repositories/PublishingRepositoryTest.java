package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publishing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PublishingRepositoryTest {

    @Autowired
    private PublishingRepository publishingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldFindPublishingByBook() {

        // GIVEN
        Book book = Book.builder()
                .title("Spring Book")
                .build();

        book = bookRepository.save(book);

        Publishing pub1 = Publishing.builder()
                .book(book)
                .name("Edition A")
                .isbn("ISBN-001")
                .noTprice(20.0)
                .royalties(0.10)
                .build();

        Publishing pub2 = Publishing.builder()
                .book(book)
                .name("Edition B")
                .isbn("ISBN-002")
                .noTprice(30.0)
                .royalties(0.15)
                .build();

        publishingRepository.save(pub1);
        publishingRepository.save(pub2);

        // WHEN
        List<Publishing> result =
                publishingRepository.findByBook(book);

        // THEN
        assertEquals(2, result.size());

        assertTrue(
                result.stream().anyMatch(p -> p.getIsbn().equals("ISBN-001"))
        );

        assertTrue(
                result.stream().anyMatch(p -> p.getIsbn().equals("ISBN-002"))
        );
    }

    @Test
    void shouldFindPublishingByIsbn() {

        // GIVEN
        Book book = bookRepository.save(
                Book.builder()
                        .title("Spring Book")
                        .build()
        );

        Publishing pub = Publishing.builder()
                .book(book)
                .name("Edition Unique")
                .isbn("ISBN-999")
                .noTprice(25.0)
                .royalties(0.20)
                .build();

        publishingRepository.save(pub);

        // WHEN
        Optional<Publishing> result =
                publishingRepository.findByIsbn("ISBN-999");

        // THEN
        assertTrue(result.isPresent());
        assertEquals("Edition Unique", result.get().getName());
        assertEquals(25.0, result.get().getNoTprice(), 0.001);
        assertEquals(0.20, result.get().getRoyalties(), 0.001);
    }

    @Test
    void shouldReturnEmptyWhenIsbnNotFound() {

        // WHEN
        Optional<Publishing> result =
                publishingRepository.findByIsbn("DOES-NOT-EXIST");

        // THEN
        assertTrue(result.isEmpty());
    }
}