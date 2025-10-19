package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publishing;
import com.publ.PublishingMgt_master.repositories.BookRepository;
import com.publ.PublishingMgt_master.repositories.PublishingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublishingServiceImpl implements PublishingService {

    private final PublishingRepository publishingRepository;
    private final BookRepository bookRepository;

    @Override
    public List<Publishing> findAll() {
        return publishingRepository.findAll();
    }

    @Override
    public Publishing findById(Long id) {
        return publishingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publishing not found with id: " + id));
    }

    @Override
    public Publishing create(Publishing publishing) {
        if (publishing.getBook() == null || publishing.getBook().getBook_id() == null) {
            throw new RuntimeException("Book ID is required to create a publishing.");
        }

        Book book = bookRepository.findById(publishing.getBook().getBook_id())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + publishing.getBook().getBook_id()));

        publishing.setBook(book);
        return publishingRepository.save(publishing);
    }

    @Override
    public Publishing update(Long id, Publishing publishingRequest) {
        Publishing existing = publishingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publishing not found with id: " + id));

        existing.setName(publishingRequest.getName());
        existing.setIsbn(publishingRequest.getIsbn());
        existing.setNoTprice(publishingRequest.getNoTprice());
        existing.setRoyalties(publishingRequest.getRoyalties());

        if (publishingRequest.getBook() != null && publishingRequest.getBook().getBook_id() != null) {
            Book book = bookRepository.findById(publishingRequest.getBook().getBook_id())
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + publishingRequest.getBook().getBook_id()));
            existing.setBook(book);
        }

        return publishingRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Publishing publishing = publishingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publishing not found with id: " + id));

        publishingRepository.delete(publishing);
    }
}
