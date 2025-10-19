package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.BookRequest;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publisher;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
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

    @Autowired
    private AuthorParticipationRepository participationRepository;

    public List<Book> books() {
        return bookRepository.findAll();
    }

    // 🔹 DELETE book safely (with message)
    public boolean deleteBook(Book book) {
        // Check if there's still author's participation linked to this book
        if (book.getParticipations() != null && !book.getParticipations().isEmpty()) {
            return false; // ❌ Impossible to delete
        }

        // ✅ Si pas de participations, on peut le supprimer
        bookRepository.delete(book);
        return true;
    }


    public Book createBook(BookRequest bookRequest) {
        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookRequest.getPublisherId()));

        // Création du livre
        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .publisher(publisher)
                .build();

        Book savedBook = bookRepository.save(book);

        // Author's participation creation
        if (bookRequest.getAuthorIds() != null && !bookRequest.getAuthorIds().isEmpty()) {
            for (Long authorId : bookRequest.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

                AuthorParticipation participation = AuthorParticipation.builder()
                        .author(author)
                        .book(savedBook)
                        .pctRateRoyalties(1.0)
                        .build();

                participationRepository.save(participation);
            }
        }

        return savedBook;
    }

    public Book updateBook(Long bookId, BookRequest bookRequest) {
        // Vérifier que le book existe
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        // Vérifier que le publisher existe
        Publisher publisher = publisherRepository.findById(bookRequest.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + bookRequest.getPublisherId()));

        // Mise à jour des champs
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setPublisher(publisher);

        Book updatedBook = bookRepository.save(existingBook);

        // delete existing participations
        participationRepository.deleteAll(updatedBook.getParticipations());

        // participation's builder for new authors
        if (bookRequest.getAuthorIds() != null && !bookRequest.getAuthorIds().isEmpty()) {
            for (Long authorId : bookRequest.getAuthorIds()) {
                Author author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

                AuthorParticipation participation = AuthorParticipation.builder()
                        .author(author)
                        .book(updatedBook)
                        .pctRateRoyalties(1.0)
                        .build();

                participationRepository.save(participation);
            }
        }

        return updatedBook;
    }
}
