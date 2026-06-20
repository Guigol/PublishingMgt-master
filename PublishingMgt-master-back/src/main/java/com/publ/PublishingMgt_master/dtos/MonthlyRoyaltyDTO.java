package com.publ.PublishingMgt_master.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRoyaltyDTO {

    private String title;
    private Integer quantitySold;
    private Integer quantityReturn;
    private Integer quantityNet;
    private Double montant;
    private String month;
    private String year;
}
