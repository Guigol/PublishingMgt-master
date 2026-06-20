package com.publ.PublishingMgt_master.e2e;

import com.publ.PublishingMgt_master.entities.*;
import com.publ.PublishingMgt_master.entities.enums.Role;
import com.publ.PublishingMgt_master.repositories.*;
import com.publ.PublishingMgt_master.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;
import org.springframework.http.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RoyaltyE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PubUserRepository pubUserRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorParticipationRepository participationRepository;

    @Autowired
    private PublishingRepository publishingRepository;

    @Autowired
    private BookSalesRepository bookSalesRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void shouldReturnYearlyRoyaltiesForAuthenticatedAuthor() throws Exception {

        // =====================
        // GIVEN (DB setup)
        // =====================

        PubUser user = pubUserRepository.save(
                PubUser.builder()
                        .login("author@test.com")
                        .password("test")
                        .role(Role.AUTHOR)
                        .build()
        );

        Author author = authorRepository.save(
                Author.builder()
                        .firstname("Henri")
                        .surname("Test")
                        .build()
        );

        user.setAuthor(author);
        pubUserRepository.save(user);

        Book book = bookRepository.save(
                Book.builder()
                        .title("Spring E2E")
                        .build()
        );

        participationRepository.save(
                AuthorParticipation.builder()
                        .author(author)
                        .book(book)
                        .pctRateRoyalties(0.10)
                        .build()
        );

        Publishing pub = publishingRepository.save(
                Publishing.builder()
                        .book(book)
                        .isbn("E2E-1")
                        .name("Edition 1")
                        .noTprice(20.0)
                        .royalties(0.10)
                        .build()
        );

        BookSales sale = BookSales.builder()
                .book(book)
                .publishing(pub)
                .year(2025)
                .month(1)
                .quantitySold(100)
                .quantityReturn(10)
                .averageDiscount(0.4)
                .build();

        bookSalesRepository.save(sale);

        // =====================
        // JWT COOKIE
        // =====================

        String jwt = jwtProvider.generateToken("author@test.com", "AUTHOR");

        Cookie cookie = new Cookie("JWT", jwt);

        // =====================
        // WHEN / THEN
        // =====================

        mockMvc.perform(get("/api/royalties/mine/year/2025")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring E2E"));
    }

}