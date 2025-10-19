package com.publ.PublishingMgt_master.dtos;

import lombok.Data;

import lombok.Data;

import java.util.List;

@Data
public class BookRequest {
    private String title;
    private Long publisherId;
    private List<Long> authorIds;
}

