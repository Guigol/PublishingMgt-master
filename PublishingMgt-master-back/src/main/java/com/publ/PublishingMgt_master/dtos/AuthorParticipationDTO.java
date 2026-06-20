package com.publ.PublishingMgt_master.dtos;

import lombok.Data;

@Data
public class AuthorParticipationDTO {

    private Long id;

    private Long authorId;
    private String authorName;

    private Long bookId;
    private String bookTitle;

    private Double pctRateRoyalties;
}