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
                System.err.println("⚠️ No seedData.json found in resources !");
                return;
            }

            Map<String, Object> jsonData = mapper.readValue(inputStream, typeRef);

            // =========================
            // 1. Publishers
            // =========================
            List<Publisher> publishers = mapper.convertValue(jsonData.get("publishers"),
                    new TypeReference<List<Publisher>>() {});
            publisherRepository.saveAll(publishers);
            Map<String, Publisher> publisherMap = publishers.stream()
                    .collect(Collectors.toMap(Publisher::getName, p -> p));

            // =========================
            // 2. Authors
            // =========================
            List<Author> authors = mapper.convertValue(jsonData.get("authors"),
                    new TypeReference<List<Author>>() {});
            authorRepository.saveAll(authors);
            Map<String, Author> authorMap = authors.stream()
                    .collect(Collectors.toMap(a -> a.getFirstname() + " " + a.getSurname(), a -> a));

            // =========================
            // 3. Books (attach publisher)
            // =========================
            List<Map<String, Object>> bookMaps = mapper.convertValue(jsonData.get("books"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<Book> books = new ArrayList<>();
            for (Map<String, Object> b : bookMaps) {
                String title = (String) b.get("title");
                Map<String, Object> pubObj = (Map<String, Object>) b.get("publisher");
                String pubName = (String) pubObj.get("name");

                Book book = new Book();
                book.setTitle(title);
                book.setPublisher(publisherMap.get(pubName));
                books.add(book);
            }
            bookRepository.saveAll(books);
            Map<String, Book> bookMap = books.stream()
                    .collect(Collectors.toMap(Book::getTitle, b -> b));

            // =========================
            // 4. Publishings (attach book)
            // =========================
            List<Map<String, Object>> publishingMaps = mapper.convertValue(jsonData.get("publishings"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<Publishing> publishings = new ArrayList<>();
            for (Map<String, Object> p : publishingMaps) {
                Publishing publishing = new Publishing();
                publishing.setName((String) p.get("name"));
                publishing.setIsbn((String) p.get("isbn"));
                publishing.setNoTprice(Double.valueOf(p.get("noTprice").toString()));
                publishing.setRoyalties(Double.valueOf(p.get("royalties").toString()));

                Map<String, Object> bookObj = (Map<String, Object>) p.get("book");
                String bookTitle = (String) bookObj.get("title");
                publishing.setBook(bookMap.get(bookTitle));

                publishings.add(publishing);
            }
            publishingRepository.saveAll(publishings);

            Map<String, Publishing> publishingMap = publishings.stream()
                    .collect(Collectors.toMap(Publishing::getIsbn, p -> p));

            // =========================
            // 5. Author Participations
            // =========================
            List<Map<String, Object>> partMaps = mapper.convertValue(jsonData.get("authorParticipations"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<AuthorParticipation> participations = new ArrayList<>();
            for (Map<String, Object> p : partMaps) {
                AuthorParticipation participation = new AuthorParticipation();
                participation.setPctRateRoyalties(Double.valueOf(p.get("pct_rate_royalties").toString()));

                Map<String, Object> authorObj = (Map<String, Object>) p.get("author");
                String key = authorObj.get("firstname") + " " + authorObj.get("surname");
                participation.setAuthor(authorMap.get(key));

                Map<String, Object> bookObj = (Map<String, Object>) p.get("book");
                String bookTitle = (String) bookObj.get("title");
                participation.setBook(bookMap.get(bookTitle));

                participations.add(participation);
            }
            authorParticipationRepository.saveAll(participations);

            // =========================
            // 6. Monthly Sales
            // =========================
            List<Map<String, Object>> saleMaps = mapper.convertValue(jsonData.get("monthlySales"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<MonthlySale> sales = new ArrayList<>();
            for (Map<String, Object> s : saleMaps) {
                MonthlySale sale = new MonthlySale();
                sale.setYear((Integer) s.get("year"));
                sale.setMonth((Integer) s.get("month"));
                sale.setQuantitySold((Integer) s.get("quantitySold"));
                sale.setQuantityReturn((Integer) s.get("quantityReturn"));
                sale.setAverageDiscount(Double.valueOf(s.get("averageDiscount").toString()));

                Map<String, Object> pubObj = (Map<String, Object>) s.get("publishing");
                String pubIsbn = (String) pubObj.get("isbn");
                sale.setPublishing(publishingMap.get(pubIsbn));

                sales.add(sale);
            }
            monthlySaleRepository.saveAll(sales);

            // =========================
            // 6bis. BookSales
            // =========================
            List<Map<String, Object>> bookSaleMaps = mapper.convertValue(jsonData.get("bookSales"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<BookSales> bookSalesList = new ArrayList<>();
            for (Map<String, Object> s : bookSaleMaps) {
                BookSales sale = new BookSales();
                sale.setYear((Integer) s.get("year"));
                sale.setMonth((Integer) s.get("month"));
                sale.setQuantitySold((Integer) s.get("quantitySold"));
                sale.setQuantityReturn((Integer) s.get("quantityReturn"));
                sale.setAverageDiscount(Double.valueOf(s.get("averageDiscount").toString()));

                Map<String, Object> pubObj = (Map<String, Object>) s.get("publishing");
                String pubIsbn = (String) pubObj.get("isbn");

                Publishing pub = publishingMap.get(pubIsbn);
                if (pub == null) throw new RuntimeException("Publishing with ISBN " + pubIsbn + " not found");

                sale.setPublishing(pub);
                sale.setBook(pub.getBook());

                bookSalesList.add(sale);
            }
            bookSalesRepository.saveAll(bookSalesList);

            // =========================
            // 7. Users
            // =========================
            List<Map<String, Object>> userMaps = mapper.convertValue(jsonData.get("users"),
                    new TypeReference<List<Map<String, Object>>>() {});
            List<PubUser> users = new ArrayList<>();
            for (Map<String, Object> u : userMaps) {
                PubUser user = new PubUser();
                user.setLogin((String) u.get("login"));
                String rawPassword = (String) u.get("password");
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setRole(Role.valueOf((String) u.get("role")));

                Map<String, Object> authorObj = (Map<String, Object>) u.get("author");
                if (authorObj != null) {
                    String key = authorObj.get("firstname") + " " + authorObj.get("surname");
                    user.setAuthor(authorMap.get(key));
                }
                users.add(user);
            }
            pubUserRepository.saveAll(users);

            System.out.println("✅ Data seed successful !");
        };
    }
}
