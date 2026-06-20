package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.entities.MonthlySale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlySaleRepository
        extends JpaRepository<MonthlySale, Long> {
    List<MonthlySale> findByPublishingId(Long publishingId);
}
