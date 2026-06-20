package com.publ.PublishingMgt_master.services;

import com.publ.PublishingMgt_master.dtos.AuthorRoyaltyDTO;
import com.publ.PublishingMgt_master.dtos.BookYearRoyaltyDTO;
import com.publ.PublishingMgt_master.dtos.MonthlyRoyaltyDTO;
import com.publ.PublishingMgt_master.entities.*;
import com.publ.PublishingMgt_master.repositories.AuthorParticipationRepository;
import com.publ.PublishingMgt_master.repositories.MonthlySaleRepository;
import com.publ.PublishingMgt_master.repositories.PublishingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoyaltyService {

    private final AuthorParticipationRepository participationRepository;
    private final PublishingRepository publishingRepository;
    private final MonthlySaleRepository monthlySaleRepository;

    // =====================================================
    // 1. AUTHOR
    // =====================================================
    public List<AuthorRoyaltyDTO> getRoyaltiesByAuthor(Author author) {
        return buildAuthorRoyalties(
                participationRepository.findByAuthor(author)
        );
    }

    // =====================================================
    // 2. AUTHOR ID
    // =====================================================
    public List<AuthorRoyaltyDTO> getRoyaltiesByAuthorId(Long authorId) {
        Author author = new Author();
        author.setAuthor_id(authorId);

        return buildAuthorRoyalties(
                participationRepository.findByAuthor(author)
        );
    }

    // =====================================================
    // 3. BOOK
    // =====================================================
    public List<AuthorRoyaltyDTO> getRoyaltiesByBook(Long bookId) {
        Book book = new Book();
        book.setBook_id(bookId);

        return buildAuthorRoyalties(
                participationRepository.findByBook(book)
        );
    }

    // =====================================================
    // COMMON METHOD
    // =====================================================
    private List<AuthorRoyaltyDTO> buildAuthorRoyalties(
            List<AuthorParticipation> participations
    ) {

        List<AuthorRoyaltyDTO> royalties = new ArrayList<>();

        for (AuthorParticipation part : participations) {

            Book book = part.getBook();

            if (book == null) {
                continue;
            }

            List<Publishing> publishings =
                    publishingRepository.findByBook(book);

            for (Publishing pub : publishings) {

                List<MonthlySale> sales =
                        monthlySaleRepository.findByPublishingId(
                                pub.getPublishingId()
                        );

                for (MonthlySale sale : sales) {

                    double montant = calculateRoyalty(part, pub, sale);

                    royalties.add(new AuthorRoyaltyDTO(
                            book.getTitle(),
                            montant,
                            monthName(sale.getSaleMonth()),
                            String.valueOf(sale.getSaleYear())
                    ));
                }
            }
        }

        royalties.sort(
                Comparator.comparing(AuthorRoyaltyDTO::getYear)
                        .thenComparing(AuthorRoyaltyDTO::getMonth)
        );

        return royalties;
    }

    // =====================================================
    // YEARLY
    // =====================================================
    public List<BookYearRoyaltyDTO> getYearlyRoyaltiesByAuthor(
            Author author,
            int year
    ) {

        List<AuthorParticipation> participations =
                participationRepository.findByAuthor(author);

        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Double> quantityMap = new HashMap<>();

        for (AuthorParticipation part : participations) {

            Book book = part.getBook();

            if (book == null) {
                continue;
            }

            List<Publishing> publishings =
                    publishingRepository.findByBook(book);

            for (Publishing pub : publishings) {

                List<MonthlySale> sales =
                        monthlySaleRepository.findByPublishingId(
                                pub.getPublishingId()
                        );

                for (MonthlySale sale : sales) {

                    if (sale.getSaleYear() == null
                            || sale.getSaleYear() != year) {
                        continue;
                    }

                    String title = book.getTitle();

                    double quantitySold =
                            defaultValue(sale.getQuantitySold());

                    double montant =
                            calculateRoyalty(part, pub, sale);

                    revenueMap.merge(title, montant, Double::sum);
                    quantityMap.merge(title, quantitySold, Double::sum);
                }
            }
        }

        return revenueMap.entrySet()
                .stream()
                .map(e -> new BookYearRoyaltyDTO(
                        e.getKey(),
                        String.valueOf(year),
                        e.getValue(),
                        quantityMap.getOrDefault(e.getKey(), 0.0)
                ))
                .sorted(Comparator.comparing(BookYearRoyaltyDTO::getTitle))
                .toList();
    }

    // =====================================================
    // MONTH DETAILS
    // =====================================================
    public List<MonthlyRoyaltyDTO> getMonthlyDetailsByBookAndYear(
            Author author,
            String title,
            int year
    ) {

        List<AuthorParticipation> participations =
                participationRepository.findByAuthor(author);

        List<MonthlyRoyaltyDTO> result = new ArrayList<>();

        for (AuthorParticipation part : participations) {

            Book book = part.getBook();

            if (book == null || !book.getTitle().equals(title)) {
                continue;
            }

            List<Publishing> publishings =
                    publishingRepository.findByBook(book);

            for (Publishing pub : publishings) {

                List<MonthlySale> sales =
                        monthlySaleRepository.findByPublishingId(
                                pub.getPublishingId()
                        );

                for (MonthlySale sale : sales) {

                    if (sale.getSaleYear() == null
                            || sale.getSaleYear() != year) {
                        continue;
                    }

                    double quantitySold =
                            defaultValue(sale.getQuantitySold());

                    double quantityReturn =
                            defaultValue(sale.getQuantityReturn());

                    double quantityNet =
                            quantitySold - quantityReturn;

                    double montant =
                            calculateRoyalty(part, pub, sale);

                    result.add(new MonthlyRoyaltyDTO(
                            book.getTitle(),
                            (int) quantitySold,
                            (int) quantityReturn,
                            (int) quantityNet,
                            montant,
                            monthName(sale.getSaleMonth()),
                            String.valueOf(year)
                    ));
                }
            }
        }

        result.sort(
                Comparator.comparing(MonthlyRoyaltyDTO::getMonth)
        );

        return result;
    }

    // =====================================================
    // ROYALTY CALCULATION
    // =====================================================
    private double calculateRoyalty(
            AuthorParticipation part,
            Publishing pub,
            MonthlySale sale
    ) {

        double quantitySold =
                defaultValue(sale.getQuantitySold());

        double quantityReturn =
                defaultValue(sale.getQuantityReturn());

        double quantityNet =
                quantitySold - quantityReturn;

        double priceHT =
                defaultValue(pub.getNoTprice());

        double discount =
                defaultValue(sale.getAverageDiscount());

        double pctRoyalties =
                defaultValue(pub.getRoyalties());

        double pctPart =
                defaultValue(part.getPctRateRoyalties());

        return quantityNet
                * priceHT
                * (1 - discount)
                * pctRoyalties
                * pctPart;
    }

    // =====================================================
    // NULL SAFE DOUBLE
    // =====================================================
    private double defaultValue(Number value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    // =====================================================
    // MONTH NAME
    // =====================================================
    private String monthName(Integer month) {

        if (month == null || month < 1 || month > 12) {
            return "Inconnu";
        }

        return Month.of(month)
                .getDisplayName(
                        TextStyle.FULL,
                        Locale.FRENCH
                );
    }
}