package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publ.PublishingMgt_master.dtos.AuthRequest;
import com.publ.PublishingMgt_master.entities.PubUser;
import com.publ.PublishingMgt_master.security.JwtProvider;
import com.publ.PublishingMgt_master.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/auth")
@Controller
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    public AuthController(AuthService authService, JwtProvider jwtProvider) {
        this.authService = authService;
        this.jwtProvider = jwtProvider;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("JWT_REFRESH".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    @GetMapping("/home")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("God save the queen!!");
    }

    /**
     * LOGIN with HttpOnly JWT cookies
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> login(@RequestBody AuthRequest loginRequest,
                                          HttpServletResponse response) {

        Authentication authentication = authService.authenticate(loginRequest);

        PubUser user = authService.getPubUser()
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtProvider.generateToken(authentication);

        ResponseCookie accessCookie = ResponseCookie.from("JWT", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(3600)
                .build();

        String refreshToken = authService.generateRefreshToken(
                user.getLogin(),
                user.getRole().name()
        );

        ResponseCookie refreshCookie = ResponseCookie.from("JWT_REFRESH", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(userResponse(user));
    }

    /**
     * SIGNUP
     */
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> signup(@RequestBody AuthRequest authRequest) {
        try {
            PubUser createdUser = authService.signup(authRequest);
            return ResponseEntity.ok(userResponse(createdUser));
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity().body(errorMessage(e.getMessage()));
        }
    }

    /**
     * LOGOUT : delete cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        ResponseCookie deleteAccessToken = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie deleteRefreshToken = ResponseCookie.from("JWT_REFRESH", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshToken.toString());

        return ResponseEntity.noContent().build();
    }

    /**
     * For MANAGER and ADMIN : :
     * return a specific book's'royalties.
     */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/authors")
    public ResponseEntity<List<JsonNode>> getAllAuthors() {

        List<PubUser> users = authService.getAllUsers();

        List<JsonNode> authors = users.stream()
                .filter(u -> u.getRole().name().equals("AUTHOR"))
                .map(PubUser::asJson)
                .toList();

        return ResponseEntity.ok(authors);
    }


    /**
     * RESTORE SESSION : /me
     */
    @GetMapping("/me")
    public ResponseEntity<JsonNode> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PubUser> user = authService.getPubUser();
        return user.map(u -> ResponseEntity.ok(u.asJson()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * REFRESH JWT access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtProvider.getUsernameFromJwt(refreshToken);
        String role = jwtProvider.extractClaim(refreshToken, claims -> claims.get("roles", String.class));

        // Nouveau JWT access token
        String newAccessToken = jwtProvider.generateToken(username, role);

        ResponseCookie cookie = ResponseCookie.from("JWT", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")     // <-- important
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    /* =========================
       Helpers JSON
     ========================= */
    private ObjectNode userResponse(PubUser pubuser) {
        return new ObjectMapper().createObjectNode()
                .put("id", pubuser.getId())
                .put("login", pubuser.getLogin())
                .put("role", pubuser.getRole().toString())
                .put("created_at", pubuser.getCreatedAt().toString());
    }

    private ObjectNode errorMessage(String message) {
        return new ObjectMapper().createObjectNode()
                .put("error", message);
    }
}