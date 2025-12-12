package com.publ.PublishingMgt_master.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publ.PublishingMgt_master.dtos.AuthRequest;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.PubUser;
import com.publ.PublishingMgt_master.entities.enums.Role;
import com.publ.PublishingMgt_master.repositories.AuthorRepository;
import com.publ.PublishingMgt_master.repositories.PubUserRepository;
import com.publ.PublishingMgt_master.security.JwtProvider;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class AuthService {
    private final PubUserRepository pubUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AuthorRepository authorRepository;

    public AuthService(
            PubUserRepository pubUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            AuthorRepository authorRepository
    ) {
        this.pubUserRepository = pubUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.authorRepository = authorRepository;
    }

    public PubUser signup(AuthRequest authRequest) {
        if (pubUserRepository.findByLogin(authRequest.getLogin()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        return pubUserRepository.save(buildPubUser(authRequest));
    }

    private PubUser buildPubUser(AuthRequest authRequest) {
        String rawPassword = (authRequest.getPassword() == null || authRequest.getPassword().isBlank())
                ? "12345"
                : authRequest.getPassword();

        PubUser user = PubUser.builder()
                .login(authRequest.getLogin())
                .password(passwordEncoder.encode(rawPassword))
                .role(authRequest.getRole() != null ? authRequest.getRole() : Role.USER)
                .build();

        if (user.getRole() == Role.AUTHOR) {
            if (authRequest.getAuthor() == null
                    || authRequest.getAuthor().getFirstname() == null
                    || authRequest.getAuthor().getFirstname().isBlank()
                    || authRequest.getAuthor().getSurname() == null
                    || authRequest.getAuthor().getSurname().isBlank()) {
                throw new IllegalArgumentException("firstname and surname are required when role == AUTHOR");
            }

            Optional<Author> existing = authorRepository.findByFirstnameAndSurname(
                    authRequest.getAuthor().getFirstname().trim(),
                    authRequest.getAuthor().getSurname().trim()
            );

            Author author = existing.orElseGet(() ->
                    authorRepository.save(Author.builder()
                            .firstname(authRequest.getAuthor().getFirstname().trim())
                            .surname(authRequest.getAuthor().getSurname().trim())
                            .build())
            );
            user.setAuthor(author);
        }
        return user;
    }

    public ResponseEntity<JsonNode> createPubUser(AuthRequest req) {
        ObjectMapper mapper = new ObjectMapper();

         if (req.getLogin() == null || req.getLogin().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapper.createObjectNode().put("error", "login is required"));
        }
        if (req.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapper.createObjectNode().put("error", "role is required"));
        }

        try {
            PubUser saved = pubUserRepository.save(buildPubUser(req));
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.asJson());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapper.createObjectNode().put("error", e.getMessage()));
        }
    }


    public ResponseEntity<JsonNode> login(AuthRequest loginRequest) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);

            return ResponseEntity.ok(loginSuccessResponseNode(authenticate));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(badCredentialsResponseNode(loginRequest));
        }
    }

    public ResponseEntity<JsonNode> updatePubUser(Long id, AuthRequest req) {
        ObjectMapper mapper = new ObjectMapper();

        PubUser pubUser = pubUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PubUser not exist with id :" + id));

        // maj login et role
        if (req.getLogin() != null && !req.getLogin().isBlank()) {
            pubUser.setLogin(req.getLogin().trim());
        }
        if (req.getRole() != null) {
            pubUser.setRole(req.getRole());
        }

        // ⚠️ ne pas toucher au mot de passe → réservé à l'utilisateur lui-même

        // si role == AUTHOR : rattacher un auteur
        if (req.getRole() == Role.AUTHOR) {
            if (req.getAuthor() == null
                    || req.getAuthor().getFirstname() == null || req.getAuthor().getFirstname().isBlank()
                    || req.getAuthor().getSurname() == null || req.getAuthor().getSurname().isBlank()) {
                ObjectNode err = mapper.createObjectNode()
                        .put("error", "firstname and surname are required when role == AUTHOR");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            Optional<Author> existing = authorRepository.findByFirstnameAndSurname(
                    req.getAuthor().getFirstname().trim(),
                    req.getAuthor().getSurname().trim()
            );

            Author author = existing.orElseGet(() ->
                    authorRepository.save(Author.builder()
                            .firstname(req.getAuthor().getFirstname().trim())
                            .surname(req.getAuthor().getSurname().trim())
                            .build())
            );

            pubUser.setAuthor(author);
        } else {
            // si ce n'est pas un auteur → on supprime le lien éventuel
            pubUser.setAuthor(null);
        }

        PubUser updated = pubUserRepository.save(pubUser);
        return ResponseEntity.ok(updated.asJson());
    }


    private ObjectNode badCredentialsResponseNode(AuthRequest loginRequest) {
        return new ObjectMapper().createObjectNode()
                .put("error", badCredentialsMessage(loginRequest.getLogin()));
    }

    private String badCredentialsMessage(String login) {
        return pubUserRepository.findByLogin(login)
                .map(user -> "Invalid password")
                .orElse("Invalid login");
    }

    private ObjectNode loginSuccessResponseNode(Authentication authentication) {
        return new ObjectMapper().createObjectNode()
                .put("login", authentication.getName())
                .put("role", authorities(authentication.getAuthorities()))
                .put("jwt", jwtProvider.generateToken(authentication));
    }

    private String authorities(Collection<? extends GrantedAuthority> grantedAuthority) {
        return grantedAuthority.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public Optional<PubUser> getPubUser() {
        String pubUserLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        return pubUserRepository.findByLogin(pubUserLogin);
    }

    public PubUser save(PubUser pubUser) {
        return pubUserRepository.save(pubUser);
    }

    public Author getAuthorByLogin(String login) {
        return pubUserRepository.findByLogin(login)
                .map(PubUser::getAuthor)
                .orElseThrow(() -> new RuntimeException("Author not found for login: " + login));
    }

}
