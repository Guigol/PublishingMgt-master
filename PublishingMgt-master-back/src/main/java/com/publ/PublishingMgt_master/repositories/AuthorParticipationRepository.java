package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorParticipationRepository extends JpaRepository<AuthorParticipation, Long> {
    List<AuthorParticipation> findByAuthor(Author author);
}
