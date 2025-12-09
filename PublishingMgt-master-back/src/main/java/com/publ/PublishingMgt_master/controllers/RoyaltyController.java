package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publ.PublishingMgt_master.dtos.AuthorRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.services.AuthService;
import com.publ.PublishingMgt_master.services.RoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/royalties")
@RequiredArgsConstructor
public class RoyaltyController {

    private final RoyaltyService royaltyService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

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
     * ✅ Accessible aux MANAGER et ADMIN :
     * permet de consulter les redevances d’un auteur spécifique.
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
     * ✅ Accessible aux MANAGER et ADMIN :
     * permet de consulter les redevances liées à un livre.
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
}
