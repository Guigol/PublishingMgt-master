package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.BookRequest;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publisher;
import com.publ.PublishingMgt_master.repositories.AuthorRepository;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import com.publ.PublishingMgt_master.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> books() {
        return bookRepository.findAll();
    }

    // DELETE book safely (with message)
    public boolean deleteBook(Book book) {
        if (book.getParticipations() != null && !book.getParticipations().isEmpty()) {
            return false;
        }

        bookRepository.delete(book);
        return true;
    }

    // CREATE
    public Book createBook(BookRequest bookRequest) {

        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookRequest.getPublisherId()));

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .publisher(publisher)
                .build();

        // Participation management via Book
        if (bookRequest.getAuthorIds() != null) {
            for (Long authorId : bookRequest.getAuthorIds()) {

                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

                AuthorParticipation participation = AuthorParticipation.builder()
                        .author(author)
                        .pctRateRoyalties(0.10)
                        .build();

                book.addParticipation(participation);
            }
        }

        return bookRepository.save(book);
    }

    // UPDATE
    public Book updateBook(Long bookId, BookRequest bookRequest) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookRequest.getPublisherId()));

        book.setTitle(bookRequest.getTitle());
        book.setPublisher(publisher);

        // Clean removal (orphanRemoval)
        book.clearParticipations();

        // Build
        if (bookRequest.getAuthorIds() != null) {
            for (Long authorId : bookRequest.getAuthorIds()) {

                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

                AuthorParticipation participation = AuthorParticipation.builder()
                        .author(author)
                        .pctRateRoyalties(0.10)
                        .build();

                book.addParticipation(participation);
            }
        }

        return bookRepository.save(book);
    }
}