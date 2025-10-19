package com.publ.PublishingMgt_master.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRoyaltyDTO {
    private String title;
    private double montant;
    private String month;
    private String year;
}
