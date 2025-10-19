package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.BookSales;
import com.publ.PublishingMgt_master.entities.Publishing;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import com.publ.PublishingMgt_master.repositories.BookSalesRepository;
import com.publ.PublishingMgt_master.repositories.PublishingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSaleService {

    private final BookSalesRepository bookSalesRepository;
    private final BookRepository bookRepository;
    private final PublishingRepository publishingRepository;

    public List<BookSales> getAllSales() {
        return bookSalesRepository.findAll();
    }

    public List<BookSales> getSalesByBook(Book book) {
        return bookSalesRepository.findByBook(book);
    }

    public BookSales getSaleById(Long id) {
        return bookSalesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'id : " + id));
    }

    public BookSales createSale(BookSales sale) {
        if (sale.getBook() != null && sale.getBook().getBook_id() != null) {
            Book book = bookRepository.findById(sale.getBook().getBook_id())
                    .orElseThrow(() -> new RuntimeException("Book not found with id " + sale.getBook().getBook_id()));
            sale.setBook(book);
        }

        if (sale.getPublishing() != null && sale.getPublishing().getIsbn() != null) {
            Publishing pub = publishingRepository.findByIsbn(sale.getPublishing().getIsbn())
                    .orElseThrow(() -> new RuntimeException("Publishing not found with ISBN " + sale.getPublishing().getIsbn()));
            sale.setPublishing(pub);
        }

        return bookSalesRepository.save(sale);
    }

    public BookSales updateSale(Long id, BookSales updatedSale) {
        BookSales existing = bookSalesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'id : " + id));

        existing.setBook(updatedSale.getBook());
        existing.setMonth(updatedSale.getMonth());
        existing.setYear(updatedSale.getYear());
        existing.setQuantitySold(updatedSale.getQuantitySold());

        return bookSalesRepository.save(existing);
    }

    public void deleteSale(Long id) {
        bookSalesRepository.deleteById(id);
    }
}
