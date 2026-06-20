package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publ.PublishingMgt_master.dtos.AuthorRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
import com.publ.PublishingMgt_master.services.AuthService;
import com.publ.PublishingMgt_master.services.RoyaltyService;
import com.publ.PublishingMgt_master.dtos.BookYearRoyaltyDTO;
import com.publ.PublishingMgt_master.dtos.MonthlyRoyaltyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/royalties")
@RequiredArgsConstructor
public class RoyaltyController {

    private final RoyaltyService royaltyService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthorParticipationRepository participationRepository;

    /**
     *  Royalties author's only :
     * return his own royalties.
     */
    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/mine")
    public ResponseEntity<JsonNode> getMyRoyalties(Authentication authentication) {
        try {
            Author author = authService.getAuthorByLogin(authentication.getName());
            List<AuthorRoyaltyDTO> royalties = royaltyService.getRoyaltiesByAuthor(author);
            return ResponseEntity.ok(mapper.valueToTree(royalties));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    /**
     * For MANAGER and ADMIN :
     * return a specific author's'royalties.
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<JsonNode> getRoyaltiesByAuthorId(@PathVariable Long authorId) {
        try {
            List<AuthorRoyaltyDTO> royalties = royaltyService.getRoyaltiesByAuthorId(authorId);
            return ResponseEntity.ok(mapper.valueToTree(royalties));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    /**
     * For MANAGER and ADMIN : :
     * return a specific book's'royalties.
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<JsonNode> getRoyaltiesByBook(@PathVariable Long bookId) {
        try {
            List<AuthorRoyaltyDTO> royalties = royaltyService.getRoyaltiesByBook(bookId);
            return ResponseEntity.ok(mapper.valueToTree(royalties));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    /**
     * AUTHOR :
     * Aggregate royalties per book for a given year
     */
    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/mine/year/{year}")
    public ResponseEntity<JsonNode> getMyYearlyRoyalties(
            Authentication authentication,
            @PathVariable int year
    ) {
        try {
            Author author = authService.getAuthorByLogin(authentication.getName());
            List<BookYearRoyaltyDTO> result =
                    royaltyService.getYearlyRoyaltiesByAuthor(author, year);

            return ResponseEntity.ok(mapper.valueToTree(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    /**
     * AUTHOR :
     * Monthly details of royalties for a book and a year
     */
    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/mine/book/{title}/year/{year}")
    public ResponseEntity<JsonNode> getMonthlyDetails(
            Authentication authentication,
            @PathVariable String title,
            @PathVariable int year
    ) {
        try {
            Author author = authService.getAuthorByLogin(authentication.getName());

            List<MonthlyRoyaltyDTO> result =
                    royaltyService.getMonthlyDetailsByBookAndYear(author, title, year);

            return ResponseEntity.ok(mapper.valueToTree(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }

    /**
     * ADMIN & MANAGER :
     * Get royalties for an author with its related books
     */
    @GetMapping("/books/author/{authorId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {

        List<Book> books = participationRepository.findAll().stream()
                .filter(p -> p.getAuthor() != null
                        && p.getAuthor().getAuthor_id().equals(authorId))
                .map(AuthorParticipation::getBook)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return ResponseEntity.ok(books);
    }


}
