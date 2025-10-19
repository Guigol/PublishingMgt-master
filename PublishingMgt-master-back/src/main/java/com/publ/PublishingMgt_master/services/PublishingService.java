package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.entities.Publishing;
import java.util.List;

public interface PublishingService {

    List<Publishing> findAll();

    Publishing findById(Long id);

    Publishing create(Publishing publishing);

    Publishing update(Long id, Publishing publishing);

    void delete(Long id);
}
