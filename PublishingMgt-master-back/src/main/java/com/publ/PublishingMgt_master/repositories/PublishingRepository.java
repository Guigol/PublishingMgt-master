package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publishing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublishingRepository extends JpaRepository<Publishing, Long> {
    List<Publishing> findByBook(Book book);
    Optional<Publishing> findByIsbn(String isbn);

}
