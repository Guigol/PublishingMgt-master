package com.publ.PublishingMgt_master.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookYearRoyaltyDTO {
    private String title;
    private String year;
    private double totalAmount;
    private Double quantitySold;
}