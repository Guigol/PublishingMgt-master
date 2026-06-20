package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorParticipationRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorParticipationRepository participationRepository;

    @Test
    void shouldFindParticipationsByAuthor() {

        Author author = Author.builder()
                .firstname("Henri")
                .surname("DeMontherlant")
                .build();

        author = authorRepository.save(author);

        Book book1 = Book.builder()
                .title("Book A")
                .build();

        Book book2 = Book.builder()
                .title("Book B")
                .build();

        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);

        participationRepository.save(
                AuthorParticipation.builder()
                        .author(author)
                        .book(book1)
                        .pctRateRoyalties(0.5)
                        .build()
        );

        participationRepository.save(
                AuthorParticipation.builder()
                        .author(author)
                        .book(book2)
                        .pctRateRoyalties(0.5)
                        .build()
        );

        List<AuthorParticipation> result =
                participationRepository.findByAuthor(author);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindParticipationsByBook() {

        Author author = authorRepository.save(
                Author.builder()
                        .firstname("Victor")
                        .surname("Hugo")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .title("Les Misérables")
                        .build()
        );

        participationRepository.save(
                AuthorParticipation.builder()
                        .author(author)
                        .book(book)
                        .pctRateRoyalties(1.0)
                        .build()
        );

        List<AuthorParticipation> result =
                participationRepository.findByBook(book);

        assertEquals(1, result.size());
        assertEquals(
                "Victor",
                result.get(0).getAuthor().getFirstname()
        );
    }
}