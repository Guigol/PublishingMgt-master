package com.publ.PublishingMgt_master.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Monthly_Sale")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySale {

    @Id
    @Column(name = "SALE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_year", nullable = false) // évite le mot réservé
    private Integer year;

    @Column(name = "sale_month", nullable = false) // évite le mot réservé
    private Integer month;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "quantity_return")
    private Integer quantityReturn;

    @Column(name = "average_discount")
    private Double averageDiscount;

    @ManyToOne
    @JoinColumn(name = "publishing_id", referencedColumnName = "publishing_id")
    private Publishing publishing;
}
