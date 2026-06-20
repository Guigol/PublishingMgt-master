package com.publ.PublishingMgt_master.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publ.PublishingMgt_master.entities.*;
import com.publ.PublishingMgt_master.entities.enums.Role;
import com.publ.PublishingMgt_master.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            PublisherRepository publisherRepository,
            AuthorRepository authorRepository,
            BookRepository bookRepository,
            PublishingRepository publishingRepository,
            AuthorParticipationRepository authorParticipationRepository,
            MonthlySaleRepository monthlySaleRepository,
            BookSalesRepository bookSalesRepository,
            PubUserRepository pubUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
            InputStream inputStream = getClass().getResourceAsStream("/seeData.json");
            if (inputStream == null) {
                System.err.println("⚠️ No seeData.json found in resources !");
                return;
            }

            Map<String, Object> jsonData = mapper.readValue(inputStream, typeRef);

            // =========================
            // 1. Publishers
            // =========================
            List<Publisher> publishers = Optional.ofNullable(jsonData.get("publishers"))
                    .map(p -> mapper.convertValue(p, new TypeReference<List<Publisher>>() {}))
                    .orElse(Collections.emptyList());
            publisherRepository.saveAll(publishers);
            Map<String, Publisher> publisherMap = publishers.stream()
                    .collect(Collectors.toMap(Publisher::getName, p -> p));

            // =========================
            // 2. Authors
            // =========================
            List<Author> authors = Optional.ofNullable(jsonData.get("authors"))
                    .map(a -> mapper.convertValue(a, new TypeReference<List<Author>>() {}))
                    .orElse(Collections.emptyList());
            authorRepository.saveAll(authors);
            Map<String, Author> authorMap = authors.stream()
                    .collect(Collectors.toMap(a -> a.getFirstname() + " " + a.getSurname(), a -> a));

            // =========================
            // 3. Books
            // =========================
            List<Map<String, Object>> bookMaps = Optional.ofNullable(jsonData.get("books"))
                    .map(b -> mapper.convertValue(b, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<Book> books = new ArrayList<>();
            for (Map<String, Object> b : bookMaps) {
                String title = (String) b.get("title");
                String pubName = (String) b.get("publisher");

                Book book = new Book();
                book.setTitle(title);
                book.setPublisher(publisherMap.get(pubName));
                books.add(book);
            }
            bookRepository.saveAll(books);
            Map<String, Book> bookMap = books.stream()
                    .collect(Collectors.toMap(Book::getTitle, b -> b));

            // =========================
            // 4. Publishings
            // =========================
            List<Map<String, Object>> publishingMaps = Optional.ofNullable(jsonData.get("publishings"))
                    .map(p -> mapper.convertValue(p, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<Publishing> publishings = new ArrayList<>();
            for (Map<String, Object> p : publishingMaps) {
                Publishing publishing = new Publishing();
                publishing.setName((String) p.get("name"));
                publishing.setIsbn((String) p.get("isbn"));
                publishing.setNoTprice(Double.valueOf(p.get("noTprice").toString()));
                publishing.setRoyalties(Double.valueOf(p.get("royalties").toString()));

                String bookTitle = (String) p.get("book");
                publishing.setBook(bookMap.get(bookTitle));

                publishings.add(publishing);
            }
            publishingRepository.saveAll(publishings);
            Map<String, Publishing> publishingMap = publishings.stream()
                    .collect(Collectors.toMap(Publishing::getIsbn, p -> p));

            // =========================
            // 5. Author Participations
            // =========================
            List<Map<String, Object>> partMaps = Optional.ofNullable(jsonData.get("authorParticipations"))
                    .map(p -> mapper.convertValue(p, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<AuthorParticipation> participations = new ArrayList<>();
            for (Map<String, Object> p : partMaps) {
                AuthorParticipation participation = new AuthorParticipation();
                participation.setPctRateRoyalties(Double.valueOf(p.get("pct_rate_royalties").toString()));

                String authorKey = (String) p.get("author");
                participation.setAuthor(authorMap.get(authorKey));

                String bookTitle = (String) p.get("book");
                participation.setBook(bookMap.get(bookTitle));

                participations.add(participation);
            }
            authorParticipationRepository.saveAll(participations);

            // =========================
            // 6. Monthly Sales
            // =========================
            List<Map<String, Object>> saleMaps = Optional.ofNullable(jsonData.get("monthlySales"))
                    .map(s -> mapper.convertValue(s, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<MonthlySale> sales = new ArrayList<>();
            for (Map<String, Object> s : saleMaps) {
                MonthlySale sale = new MonthlySale();
                sale.setSaleYear((Integer) s.getOrDefault("year", 0));
                sale.setSaleMonth((Integer) s.getOrDefault("month", 0));
                sale.setQuantitySold((Integer) s.getOrDefault("quantitySold", 0));
                sale.setQuantityReturn((Integer) s.getOrDefault("quantityReturn", 0));
                sale.setAverageDiscount(Double.valueOf(s.getOrDefault("averageDiscount", 0.0).toString()));

                String pubIsbn = (String) s.get("publishing");
                if (pubIsbn != null && publishingMap.get(pubIsbn) != null) {
                    sale.setPublishingId(publishingMap.get(pubIsbn).getPublishingId());
                }

                sales.add(sale);
            }
            monthlySaleRepository.saveAll(sales);

            // =========================
            // 6bis. BookSales
            // =========================
            List<Map<String, Object>> bookSaleMaps = Optional.ofNullable(jsonData.get("bookSales"))
                    .map(s -> mapper.convertValue(s, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<BookSales> bookSalesList = new ArrayList<>();
            for (Map<String, Object> s : bookSaleMaps) {
                BookSales sale = new BookSales();
                sale.setYear((Integer) s.getOrDefault("year", 0));
                sale.setMonth((Integer) s.getOrDefault("month", 0));
                sale.setQuantitySold((Integer) s.getOrDefault("quantitySold", 0));
                sale.setQuantityReturn((Integer) s.getOrDefault("quantityReturn", 0));
                sale.setAverageDiscount(Double.valueOf(s.getOrDefault("averageDiscount", 0.0).toString()));

                String pubIsbn = (String) s.get("isbn");
                Publishing pub = publishingMap.get(pubIsbn);
                if (pub == null) {
                    System.err.println("⚠️ Publishing with ISBN " + pubIsbn + " not found");
                    continue;
                }
                sale.setPublishing(pub);
                sale.setBook(pub.getBook());

                bookSalesList.add(sale);
            }
            bookSalesRepository.saveAll(bookSalesList);

            // =========================
            // 7. Users
            // =========================
            List<Map<String, Object>> userMaps = Optional.ofNullable(jsonData.get("users"))
                    .map(u -> mapper.convertValue(u, new TypeReference<List<Map<String, Object>>>() {}))
                    .orElse(Collections.emptyList());
            List<PubUser> users = new ArrayList<>();
            for (Map<String, Object> u : userMaps) {
                PubUser user = new PubUser();
                user.setLogin((String) u.get("login"));
                String rawPassword = (String) u.get("password");
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setRole(Role.valueOf((String) u.get("role")));

                String authorKey = (String) u.get("author");
                if (authorKey != null) {
                    user.setAuthor(authorMap.get(authorKey));
                }
                users.add(user);
            }
            pubUserRepository.saveAll(users);

            System.out.println("✅ Data seed successful !");
        };
    }
}