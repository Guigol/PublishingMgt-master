package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.BookSales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookSalesRepository extends JpaRepository<BookSales, Long> {
    List<BookSales> findByBook(Book book);
    List<BookSales> findByBookAndYearAndMonth(Book book, int year, int month);
}
