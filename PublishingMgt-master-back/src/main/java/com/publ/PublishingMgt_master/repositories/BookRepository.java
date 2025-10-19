package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {}
