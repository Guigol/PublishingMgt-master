package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.BookSales;
import com.publ.PublishingMgt_master.entities.Publishing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DataJpaTest
class MonthlySaleRepositoryTest {

    @Autowired
    private MonthlySaleRepository monthlySaleRepository;

    @Autowired
    private BookSalesRepository bookSalesRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublishingRepository publishingRepository;

    @Test
    void shouldReadMonthlySaleFromBookSalesViaSubselect() {

        // GIVEN
        Book book = bookRepository.save(
                Book.builder()
                        .title("Spring Book")
                        .build()
        );

        Publishing pub = publishingRepository.save(
                Publishing.builder()
                        .book(book)
                        .name("Edition 1")
                        .isbn("ISBN-100")
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
                .averageDiscount(0.0)
                .build();

        bookSalesRepository.save(sale);
        bookSalesRepository.flush();

        // WHEN
        List<com.publ.PublishingMgt_master.entities.MonthlySale> result =
                monthlySaleRepository.findByPublishingId(pub.getPublishingId());

        // THEN
        assertEquals(1, result.size());

        com.publ.PublishingMgt_master.entities.MonthlySale ms = result.get(0);

        assertEquals(2025, ms.getSaleYear());
        assertEquals(1, ms.getSaleMonth());
        assertEquals(100, ms.getQuantitySold());
        assertEquals(10, ms.getQuantityReturn());
        assertEquals(pub.getPublishingId(), ms.getPublishingId());
    }

    @Test
    void shouldReturnEmptyWhenNoSalesExist() {

        // GIVEN
        Book book = bookRepository.save(
                Book.builder()
                        .title("Empty Book")
                        .build()
        );

        Publishing pub = publishingRepository.save(
                Publishing.builder()
                        .book(book)
                        .name("Edition X")
                        .isbn("ISBN-EMPTY")
                        .noTprice(20.0)
                        .royalties(0.10)
                        .build()
        );

        // WHEN
        List<com.publ.PublishingMgt_master.entities.MonthlySale> result =
                monthlySaleRepository.findByPublishingId(pub.getPublishingId());

        // THEN
        assertTrue(result.isEmpty());
    }
}