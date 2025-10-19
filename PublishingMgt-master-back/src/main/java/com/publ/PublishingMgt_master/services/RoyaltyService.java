package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.AuthorRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.Author;
import com.publ.PublishingMgt_master.entities.AuthorParticipation;
import com.publ.PublishingMgt_master.entities.Book;
import com.publ.PublishingMgt_master.entities.Publishing;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
import com.publ.PublishingMgt_master.repositories.PublishingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoyaltyService {

    private final AuthorParticipationRepository participationRepository;
    private final PublishingRepository publishingRepository;

    /**
     * 🔹 Retourne les redevances pour l'auteur actuellement connecté.
     */
    public List<AuthorRoyaltyDTO> getRoyaltiesByAuthor(Author author) {
        List<AuthorParticipation> participations = participationRepository.findByAuthor(author);

        return participations.stream()
                .flatMap(part -> {
                    Book book = part.getBook();
                    if (book == null) return Stream.empty();

                    List<Publishing> publishings = publishingRepository.findByBook(book);
                    return publishings.stream().map(pub -> buildRoyaltyDTO(part, pub));
                })
                .collect(Collectors.toList());
    }

    /**
     * 🔹 Pour un manager : toutes les redevances d’un auteur donné
     */
    public List<AuthorRoyaltyDTO> getRoyaltiesByAuthorId(Long authorId) {
        List<AuthorParticipation> participations = participationRepository.findAll().stream()
                .filter(p -> p.getAuthor() != null && p.getAuthor().getAuthor_id().equals(authorId))
                .toList();

        return participations.stream()
                .flatMap(part -> {
                    Book book = part.getBook();
                    if (book == null) return Stream.empty();

                    List<Publishing> publishings = publishingRepository.findByBook(book);
                    return publishings.stream().map(pub -> buildRoyaltyDTO(part, pub));
                })
                .collect(Collectors.toList());
    }

    /**
     * 🔹 Pour un manager : redevances par livre
     */
    public List<AuthorRoyaltyDTO> getRoyaltiesByBook(Long bookId) {
        List<AuthorParticipation> participations = participationRepository.findAll().stream()
                .filter(p -> p.getBook() != null && p.getBook().getBook_id().equals(bookId))
                .toList();

        return participations.stream()
                .flatMap(part -> {
                    List<Publishing> publishings = publishingRepository.findByBook(part.getBook());
                    return publishings.stream().map(pub -> buildRoyaltyDTO(part, pub));
                })
                .collect(Collectors.toList());
    }

    /**
     * 🔹 Construit un DTO avec calcul du montant :
     * montant = prixHT * taux_royalties_publishing * part_auteur
     */
    private AuthorRoyaltyDTO buildRoyaltyDTO(AuthorParticipation part, Publishing pub) {
        double noTprice = pub.getNoTprice() != null ? pub.getNoTprice() : 0.0;
        double pctRoyalties = pub.getRoyalties() != null ? pub.getRoyalties() : 0.0;
        double pctPart = part.getPctRateRoyalties() != null ? part.getPctRateRoyalties() : 0.0;

        double montant = noTprice * pctRoyalties * pctPart;

        LocalDate now = LocalDate.now();
        String month = now.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        String year = String.valueOf(now.getYear());

        String title = part.getBook() != null ? part.getBook().getTitle() : "Inconnu";

        return new AuthorRoyaltyDTO(title, montant, month, year);
    }
}
