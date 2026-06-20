package com.publ.PublishingMgt_master.controllers;

import com.publ.PublishingMgt_master.dtos.AuthorParticipationDTO;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.mappers.AuthorParticipationMapper;
import com.publ.PublishingMgt_master.repositories.AuthorRepository;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import com.publ.PublishingMgt_master.services.AuthorParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author-part")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthorParticipationController {

    private final AuthorParticipationService service;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;


    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<AuthorParticipationDTO> getAll() {
        return service.findAll()
                .stream()
                .map(AuthorParticipationMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/{id}")
    public AuthorParticipationDTO getById(@PathVariable Long id) {
        return AuthorParticipationMapper.toDto(service.findById(id));
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping
    public AuthorParticipationDTO create(@RequestBody AuthorParticipationDTO dto) {

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        AuthorParticipation entity =
                AuthorParticipationMapper.toEntity(dto, author, book);

        return AuthorParticipationMapper.toDto(service.save(entity));
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/{id}")
    public AuthorParticipationDTO update(@PathVariable Long id,
                                         @RequestBody AuthorParticipationDTO dto) {

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        dto.setId(id);

        AuthorParticipation entity =
                AuthorParticipationMapper.toEntity(dto, author, book);

        return AuthorParticipationMapper.toDto(service.save(entity));
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/author/{authorId}")
    public List<AuthorParticipationDTO> getByAuthor(@PathVariable Long authorId) {
        return service.findByAuthor(authorId)
                .stream()
                .map(AuthorParticipationMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/book/{bookId}")
    public List<AuthorParticipationDTO> getByBook(@PathVariable Long bookId) {
        return service.findByBook(bookId)
                .stream()
                .map(AuthorParticipationMapper::toDto)
                .toList();
    }
}
