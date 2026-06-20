package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.BookYearRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.*;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
import com.publ.PublishingMgt_master.repositories.MonthlySaleRepository;
import com.publ.PublishingMgt_master.repositories.PublishingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoyaltyServiceTest {

    @Mock
    private AuthorParticipationRepository participationRepository;

    @Mock
    private PublishingRepository publishingRepository;

    @Mock
    private MonthlySaleRepository monthlySaleRepository;

    @InjectMocks
    private RoyaltyService royaltyService;

    @Test
    void shouldCalculateYearlyRoyaltyForOneBook() {

        Author author = new Author();
        author.setAuthor_id(1L);

        Book book = new Book();
        book.setBook_id(1L);
        book.setTitle("Spring TEST");

        AuthorParticipation participation =
                AuthorParticipation.builder()
                        .author(author)
                        .book(book)
                        .pctRateRoyalties(0.10)
                        .build();

        Publishing publishing =
                Publishing.builder()
                        .publishingId(1L)
                        .book(book)
                        .isbn("123000002255")
                        .name("Edition Test")
                        .noTprice(20.0)
                        .royalties(0.10)
                        .build();

        MonthlySale sale = new MonthlySale();

        sale.setSaleYear(2025);
        sale.setSaleMonth(1);
        sale.setQuantitySold(100);
        sale.setQuantityReturn(10);
        sale.setAverageDiscount(0.0);

        when(participationRepository.findByAuthor(author))
                .thenReturn(List.of(participation));

        when(publishingRepository.findByBook(book))
                .thenReturn(List.of(publishing));

        when(monthlySaleRepository.findByPublishingId(1L))
                .thenReturn(List.of(sale));

        List<BookYearRoyaltyDTO> result =
                royaltyService.getYearlyRoyaltiesByAuthor(
                        author,
                        2025
                );

        assertEquals(1, result.size());

        BookYearRoyaltyDTO dto = result.get(0);

        assertEquals("Spring TEST", dto.getTitle());
        assertEquals("2025", dto.getYear());

        assertEquals(
                18.0,
                dto.getTotalAmount(),
                0.001
        );

        assertEquals(
                100.0,
                dto.getQuantitySold(),
                0.001
        );
    }

    @Test
    void shouldIgnoreSalesFromAnotherYear() {

        Author author = new Author();
        author.setAuthor_id(1L);

        Book book = new Book();
        book.setBook_id(1L);
        book.setTitle("Spring TEST");

        AuthorParticipation participation =
                AuthorParticipation.builder()
                        .author(author)
                        .book(book)
                        .pctRateRoyalties(0.10)
                        .build();

        Publishing publishing =
                Publishing.builder()
                        .publishingId(1L)
                        .book(book)
                        .isbn("123")
                        .name("Edition Test")
                        .noTprice(20.0)
                        .royalties(0.10)
                        .build();

        MonthlySale sale = new MonthlySale();

        sale.setSaleYear(2024);
        sale.setQuantitySold(100);
        sale.setQuantityReturn(10);

        when(participationRepository.findByAuthor(author))
                .thenReturn(List.of(participation));

        when(publishingRepository.findByBook(book))
                .thenReturn(List.of(publishing));

        when(monthlySaleRepository.findByPublishingId(1L))
                .thenReturn(List.of(sale));

        List<BookYearRoyaltyDTO> result =
                royaltyService.getYearlyRoyaltiesByAuthor(
                        author,
                        2025
                );

        assertEquals(0, result.size());
    }
}