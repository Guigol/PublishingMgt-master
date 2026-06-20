package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publ.PublishingMgt_master.entities.Publisher;
import com.publ.PublishingMgt_master.services.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;
    private final ObjectMapper mapper = new ObjectMapper();

    // GET all publishers
    @GetMapping("/publisher/all")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        return ResponseEntity.ok(publisherService.findAll());
    }

    // GET one publisher by ID
    @GetMapping("/publisher/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<JsonNode> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(asJson(publisherService.findById(id)));
    }

    // CREATE publishing
    @PostMapping("/publisher")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<JsonNode> createPublisher(@RequestBody Publisher publisher) {
        Publisher saved = publisherService.create(publisher);
        return ResponseEntity.status(HttpStatus.CREATED).body(asJson(saved));
    }

    // UPDATE publisher
    @PutMapping("/publisher/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<JsonNode> updatePublisher(@PathVariable Long id, @RequestBody Publisher publisher) {
        Publisher updated = publisherService.update(id, publisher);
        return ResponseEntity.ok(asJson(updated));
    }

    // DELETE publisher
    @DeleteMapping("/publisher/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<JsonNode> deletePublisher(@PathVariable Long id) {
        ObjectNode node = mapper.createObjectNode();
        try {
            publisherService.delete(id);
            node.put("message", "Publisher deleted successfully");
            return ResponseEntity.ok(node);
        } catch (RuntimeException e) {
            node.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(node);
        }
    }


    // JSON helper
    private ObjectNode asJson(Publisher publisher) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", publisher.getPublisher_id());
        node.put("name", publisher.getName());

        return node;
    }
}

