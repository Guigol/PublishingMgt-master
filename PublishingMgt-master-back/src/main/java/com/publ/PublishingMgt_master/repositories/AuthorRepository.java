package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFirstnameAndSurname(String firstname, String surname);
}
