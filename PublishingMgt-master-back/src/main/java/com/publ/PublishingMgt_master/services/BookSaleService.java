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

        // Check Publishing
        if (sale.getPublishing() == null ||
                sale.getPublishing().getIsbn() == null ||
                sale.getPublishing().getIsbn().isBlank()) {

            throw new RuntimeException("ISBN obligatoire");
        }

        // Search publishing through ISBN
        Publishing publishing = publishingRepository
                .findByIsbn(sale.getPublishing().getIsbn())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Publishing not found with ISBN : "
                                        + sale.getPublishing().getIsbn()
                        )
                );

        // Business consistency check
        if (publishing.getBook() == null) {
            throw new RuntimeException(
                    "Aucun livre associé à cet ISBN : "
                            + publishing.getIsbn()
            );
        }

        // Automatic association
        sale.setPublishing(publishing);
        sale.setBook(publishing.getBook());

        // Save
        return bookSalesRepository.save(sale);
    }

    public BookSales updateSale(Long id, BookSales updatedSale) {

        // Search existing sale
        BookSales existing = bookSalesRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vente non trouvée avec l'id : " + id
                        )
                );

        // ISBN presence check
        if (updatedSale.getPublishing() == null ||
                updatedSale.getPublishing().getIsbn() == null ||
                updatedSale.getPublishing().getIsbn().isBlank()) {

            throw new RuntimeException("ISBN obligatoire");
        }

        // Search for publishing via ISBN
        Publishing publishing = publishingRepository
                .findByIsbn(updatedSale.getPublishing().getIsbn())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Publishing not found with ISBN : "
                                        + updatedSale.getPublishing().getIsbn()
                        )
                );

        // Business consistency check
        if (publishing.getBook() == null) {

            throw new RuntimeException(
                    "Aucun livre associé à cet ISBN : "
                            + publishing.getIsbn()
            );
        }

        // Updating business fields
        existing.setMonth(updatedSale.getMonth());
        existing.setYear(updatedSale.getYear());

        existing.setQuantitySold(updatedSale.getQuantitySold());
        existing.setQuantityReturn(updatedSale.getQuantityReturn());

        existing.setAverageDiscount(updatedSale.getAverageDiscount());

        // Automatic Book Synchronization <-> Publishing
        existing.setPublishing(publishing);
        existing.setBook(publishing.getBook());

        // Save
        return bookSalesRepository.save(existing);
    }

    public void deleteSale(Long id) {
        bookSalesRepository.deleteById(id);
    }
}
