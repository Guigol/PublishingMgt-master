package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.publ.PublishingMgt_master.dtos.AuthRequest;
import com.publ.PublishingMgt_master.dtos.BookRequest;
import com.publ.PublishingMgt_master.exceptionErrors.ResourceNotFoundException;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.exceptionErrors.BookDeletionNotAllowedException;
import com.publ.PublishingMgt_master.repositories.AuthorRepository;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import com.publ.PublishingMgt_master.repositories.PubUserRepository;
import com.publ.PublishingMgt_master.services.AuthService;
import com.publ.PublishingMgt_master.services.BookServiceImpl;
import com.publ.PublishingMgt_master.services.UserDetailsServiceImpl;
import com.publ.PublishingMgt_master.entities.PubUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000/api", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
@RestController
@RequestMapping("/api/tools")
public class AdminController {
    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PubUserRepository pubUserRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    UserDetailsServiceImpl userDetails;

    @Autowired
    private BookServiceImpl bookService;


    private AuthRequest authRequest;


    //PUBUSERS

    // get all PubUsers
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/pubuser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JsonNode>> pubUser() {
        return ResponseEntity.ok(
                pubUserRepository.findAll().stream()
                        .map(PubUser::asJson)
                        .toList()
        );
    }

    // get user by ID
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/pubuser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> pubUser(@PathVariable Long id) {
        return pubUserRepository.findById(id)
                .map(PubUser::asJson)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // create PubUser rest api

    @PostMapping("/pubuser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<JsonNode> createPubUser(@RequestBody AuthRequest req) {
        return authService.createPubUser(req);
    }

    // update PubUser rest api
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/pubuser/{id}")
    public ResponseEntity<JsonNode> updatePubUser(@PathVariable Long id, @RequestBody AuthRequest req) {
        return authService.updatePubUser(id, req);
    }

    // delete PubUser rest api

    @DeleteMapping("/pubuser/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<HttpStatus> deletePubUser(@PathVariable Long id) {
        PubUser pubUser = pubUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PubUser does not exist with id :" + id));

        pubUserRepository.delete(pubUser);
        return ResponseEntity.noContent().build();
    }

    // counting users

    @GetMapping("/numberOfUsers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public Integer getNumberOfUsers() {
        return userDetails.getPubUsers().size();
    }


    //BOOKS

    // 🔹 GET all books
    @GetMapping("/book")
    public ResponseEntity<List<JsonNode>> getBooks() {
        List<JsonNode> books = bookService.books()
                .stream()
                .map(Book::asJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }

    // 🔹 CREATE book
    @PostMapping("/book")
    public ResponseEntity<JsonNode> createBook(@RequestBody BookRequest bookRequest) {
        try {
            Book saved = bookService.createBook(bookRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.asJson());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.fasterxml.jackson.databind.ObjectMapper()
                            .createObjectNode()
                            .put("error", e.getMessage()));
        }
    }

    // 🔹 UPDATE book by ID
    @PutMapping("/book/{id}")
    public ResponseEntity<JsonNode> updateBook(@PathVariable Long id, @RequestBody BookRequest bookRequest) {
        try {
            Book updated = bookService.updateBook(id, bookRequest);
            return ResponseEntity.ok(updated.asJson());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.fasterxml.jackson.databind.ObjectMapper()
                            .createObjectNode()
                            .put("error", e.getMessage()));
        }
    }

    // 🔹 DELETE book by ID
    @DeleteMapping("/book/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        Book book = bookService.books().stream()
                .filter(b -> b.getBook_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        boolean deleted = bookService.deleteBook(book);

        if (!deleted) {
            // 🔸 response 400 with JSON message
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot delete this book because it still has author participations."));
        }

        return ResponseEntity.noContent().build(); // ✅ 204 No Content
    }
}



