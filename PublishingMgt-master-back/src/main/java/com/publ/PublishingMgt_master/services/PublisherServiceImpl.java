package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.entities.Publisher;
import com.publ.PublishingMgt_master.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    @Override
    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }

    @Override
    public Publisher findById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));
    }

    @Override
    public Publisher create(Publisher publisher) {
        if (publisher.getPublisher_id() != null) {
            throw new RuntimeException("New publisher should not have an ID");
        }
        return publisherRepository.save(publisher);
    }

    @Override
    public Publisher update(Long id, Publisher publisher) {
        Publisher existing = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));

        existing.setName(publisher.getName());
        return publisherRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));

        try {
            publisherRepository.delete(publisher);
        } catch (DataIntegrityViolationException e) {
            // Gestion propre de la contrainte FK
            throw new RuntimeException("Cannot delete publisher because it is linked to existing books.");
        }
    }

}
