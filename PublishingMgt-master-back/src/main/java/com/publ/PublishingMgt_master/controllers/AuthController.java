package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publ.PublishingMgt_master.dtos.AuthRequest;
import com.publ.PublishingMgt_master.entities.PubUser;
import com.publ.PublishingMgt_master.services.AuthService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@Controller
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("God save the queen!!");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> login(@RequestBody AuthRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> signup(@RequestBody AuthRequest authRequest) {
        try {
            PubUser creatpubUser = authService.signup(authRequest);
            return ResponseEntity.ok(responseMessage(creatpubUser)); // ✅ Send full user object
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity().body(errorMessage(e.getMessage()));
        }
    }

    private ObjectNode responseMessage(PubUser pubuser) {
        return new ObjectMapper().createObjectNode()
                .put("id", pubuser.getId())
                .put("login", pubuser.getLogin())
                .put("role", pubuser.getRole() != null ? pubuser.getRole().toString() : "USER")
                .put("created_at", pubuser.getCreatedAt().toString());
    }

    private ObjectNode errorMessage(String message) {
        return new ObjectMapper().createObjectNode()
                .put("error", message);
    }

}