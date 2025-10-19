package com.publ.PublishingMgt_master.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Entity
@Table(name = "book_sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "sale_month")
    private Integer month;

    @Column(name = "sale_year")
    private Integer year;

    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

    @ManyToOne
    @JoinColumn(name = "publishing_id", nullable = false)
    private Publishing publishing;

    @Column(name = "quantity_return")
    private Integer quantityReturn;

    @Column(name = "average_discount")
    private Double averageDiscount;


    public YearMonth getPeriod() {
        return YearMonth.of(year, month);
    }
}
