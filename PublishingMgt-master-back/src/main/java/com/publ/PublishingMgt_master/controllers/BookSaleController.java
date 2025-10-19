package com.publ.PublishingMgt_master.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.BookSales;
import com.publ.PublishingMgt_master.services.AuthService;
import com.publ.PublishingMgt_master.services.BookSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-sales")
@RequiredArgsConstructor
public class BookSaleController {

    private final BookSaleService bookSaleService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * List all sales — MANAGER and ADMIN access
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllSales() {
        List<BookSales> sales = bookSaleService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    /**
     * List all sales from a given book — MANAGER and ADMIN access
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getSalesByBook(@PathVariable Long bookId) {
        Book book = new Book();
        book.setBook_id(bookId);

        List<BookSales> sales = bookSaleService.getSalesByBook(book);
        return ResponseEntity.ok(sales);
    }

    /**
     * Create new sale — MANAGER and ADMIN access
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> createSale(@RequestBody BookSales sale) {
        BookSales saved = bookSaleService.createSale(sale);
        return ResponseEntity.ok(saved);
    }

    /**
     * Update sale by ID — MANAGER and ADMIN access
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @RequestBody BookSales updatedSale) {
        BookSales saved = bookSaleService.updateSale(id, updatedSale);
        return ResponseEntity.ok(saved);
    }

    /**
     *  Delete sale by ID — MANAGER and ADMIN access
     */
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable Long id) {
        bookSaleService.deleteSale(id);
        return ResponseEntity.ok(mapper.createObjectNode().put("message", "Vente supprimée"));
    }
}
