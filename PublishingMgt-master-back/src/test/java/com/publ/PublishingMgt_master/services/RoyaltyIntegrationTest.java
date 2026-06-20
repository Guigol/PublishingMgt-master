package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.BookYearRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.*;
import com.publ.PublishingMgt_master.repositories.*;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class RoyaltyIntegrationTest {

    @Autowired
    private RoyaltyService royaltyService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorParticipationRepository participationRepository;

    @Autowired
    private PublishingRepository publishingRepository;

    @Autowired
    private BookSalesRepository bookSalesRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldCalculateRoyaltyEndToEnd() {

        // 1. AUTHOR
        Author author = new Author();
        author.setFirstname("Henri");
        author.setSurname("DeMontherlant");
        author = authorRepository.save(author);

        // 2. BOOK
        Book book = new Book();
        book.setTitle("Spring Book");
        book = bookRepository.save(book);

        // 3. PARTICIPATION
        AuthorParticipation part = new AuthorParticipation();
        part.setAuthor(author);
        part.setBook(book);
        part.setPctRateRoyalties(0.10);
        participationRepository.save(part);

        // 4. PUBLISHING
        Publishing pub = new Publishing();
        pub.setBook(book);
        pub.setNoTprice(20.0);
        pub.setRoyalties(0.10);
        pub.setIsbn("ISBN-123");
        pub.setName("Edition 1");
        pub = publishingRepository.save(pub);


        // 5. SALES
        BookSales sale = new BookSales();
        sale.setBook(book);
        sale.setPublishing(pub);
        sale.setYear(2025);
        sale.setMonth(1);
        sale.setQuantitySold(100);
        sale.setQuantityReturn(10);
        sale.setAverageDiscount(0.0);

        bookSalesRepository.save(sale);
        bookSalesRepository.flush();

        entityManager.clear();


        // 6. CALL SERVICE
        var result = royaltyService.getYearlyRoyaltiesByAuthor(author, 2025);

        // 7. ASSERT
        assertEquals(1, result.size());

        BookYearRoyaltyDTO dto = result.get(0);

        assertEquals("Spring Book", dto.getTitle());
        assertEquals(18.0, dto.getTotalAmount(), 0.001);
    }
}
