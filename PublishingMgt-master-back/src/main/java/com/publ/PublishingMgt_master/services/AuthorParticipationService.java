package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
import com.publ.PublishingMgt_master.repositories.AuthorRepository;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorParticipationService {

    private final AuthorParticipationRepository repository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public List<AuthorParticipation> findAll() {
        return repository.findAll();
    }

    public AuthorParticipation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
    }

    public AuthorParticipation save(AuthorParticipation participation) {
        return repository.save(participation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<AuthorParticipation> findByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return repository.findByAuthor(author);
    }

    public List<AuthorParticipation> findByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return repository.findByBook(book);
    }
}