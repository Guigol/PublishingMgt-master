package com.publ.PublishingMgt_master.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Table(name = "monthly_sale")
@Data
@Immutable
@Subselect(
        "SELECT " +
                "bs.id AS sale_id, " +
                "bs.sale_year AS sale_year, " +
                "bs.sale_month AS sale_month, " +
                "bs.quantity_sold AS quantity_sold, " +
                "bs.quantity_return AS quantity_return, " +
                "bs.average_discount AS average_discount, " +
                "bs.book_id AS book_id, " +
                "bs.publishing_id AS publishing_id " +
                "FROM book_sales bs"
)
public class MonthlySale {

    @Id
    @Column(name = "SALE_ID")
    private Long saleId;

    @Column(name = "SALE_YEAR")
    private Integer saleYear;

    @Column(name = "SALE_MONTH")
    private Integer saleMonth;

    @Column(name = "QUANTITY_SOLD")
    private Integer quantitySold;

    @Column(name = "QUANTITY_RETURN")
    private Integer quantityReturn;

    @Column(name = "AVERAGE_DISCOUNT")
    private Double averageDiscount;

    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "PUBLISHING_ID")
    private Long publishingId;
}