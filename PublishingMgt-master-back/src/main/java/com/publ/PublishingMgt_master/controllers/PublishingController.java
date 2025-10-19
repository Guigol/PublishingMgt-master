package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publ.PublishingMgt_master.entities.Publishing;
import com.publ.PublishingMgt_master.services.PublishingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublishingController {

    private final PublishingService publishingService;
    private final ObjectMapper mapper = new ObjectMapper();

    // 🔹 GET all publishings
    @GetMapping("/publish/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<Publishing>> getAllPublishings() {
        return ResponseEntity.ok(publishingService.findAll());
    }

    // 🔹 GET one publishing by ID
    @GetMapping("/publish/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<JsonNode> getPublishingById(@PathVariable Long id) {
        return ResponseEntity.ok(asJson(publishingService.findById(id)));
    }

    // 🔹 CREATE publishing
    @PostMapping("/publish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<JsonNode> createPublishing(@RequestBody Publishing publishing) {
        Publishing saved = publishingService.create(publishing);
        return ResponseEntity.status(HttpStatus.CREATED).body(asJson(saved));
    }

    // 🔹 UPDATE publishing
    @PutMapping("/publish/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<JsonNode> updatePublishing(@PathVariable Long id, @RequestBody Publishing publishing) {
        Publishing updated = publishingService.update(id, publishing);
        return ResponseEntity.ok(asJson(updated));
    }

    // 🔹 DELETE publishing
    @DeleteMapping("/publish/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<JsonNode> deletePublishing(@PathVariable Long id) {
        publishingService.delete(id);
        ObjectNode node = mapper.createObjectNode().put("message", "Publishing deleted successfully");
        return ResponseEntity.ok(node);
    }

    // 🔹 JSON helper
    private ObjectNode asJson(Publishing publishing) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", publishing.getPublishing_id());
        node.put("name", publishing.getName());
        node.put("isbn", publishing.getIsbn());
        node.put("noTprice", publishing.getNoTprice());
        node.put("royalties", publishing.getRoyalties());
        if (publishing.getBook() != null) {
            node.put("book_id", publishing.getBook().getBook_id());
            node.put("book_title", publishing.getBook().getTitle());
        }
        return node;
    }
}
