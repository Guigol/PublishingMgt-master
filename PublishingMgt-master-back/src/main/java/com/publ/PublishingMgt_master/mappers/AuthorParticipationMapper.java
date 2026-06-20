package com.publ.PublishingMgt_master.mappers;

import com.publ.PublishingMgt_master.dtos.AuthorParticipationDTO;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;

public class AuthorParticipationMapper {

    public static AuthorParticipationDTO toDto(AuthorParticipation entity) {
        AuthorParticipationDTO dto = new AuthorParticipationDTO();

        dto.setId(entity.getId());
        dto.setPctRateRoyalties(entity.getPctRateRoyalties());

        dto.setAuthorId(entity.getAuthor().getAuthor_id());
        dto.setAuthorName(
                entity.getAuthor().getFirstname() + " " +
                        entity.getAuthor().getSurname()
        );

        dto.setBookId(entity.getBook().getBook_id());
        dto.setBookTitle(entity.getBook().getTitle());

        return dto;
    }

    public static AuthorParticipation toEntity(
            AuthorParticipationDTO dto,
            Author author,
            Book book) {

        return AuthorParticipation.builder()
                .id(dto.getId())
                .author(author)
                .book(book)
                .pctRateRoyalties(dto.getPctRateRoyalties())
                .build();
    }
}