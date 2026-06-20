
# ⏱️TESTS

## 📋 Table of contents
- [📟 Purpose of the tests](#-purpose-of-the-tests)
- [👉 Tests location](#-tests-location)
- [🚀 Run the tests](#-run-the-tests)
- [📛 Naming Convention](#-naming-convention)
- [🧠 AuthorParticipationRepositoryTest](#-authorparticipationrepositorytest)
- [🌱 PublishingRepositoryTest](#-publishingrepositorytest)
-  [⚡ MonthlySaleRepositoryTest](#-monthlysalerepositorytest)
- [🧪 RoyaltyIntegrationTest](#-royaltyintegrationtest)
- [🧾 RoyaltyServiceTest](#-royaltyservicetest)
-  [🐛 E2E TEST](#-e2e-test)
   1. [User authentication context](#1-user-authentication-context)
   2. [Data setup in the database](#2-data-setup-in-the-database)
   3. [Business logic execution](#3-business-logic-execution)
   4. [API response validation](#4-api-response-validation)


---
## 📟 Purpose of the tests

Those tests ensure that:

- The full Spring Boot stack works correctly together (Controller → Service → Repository → DB).
- Security allows access only to authenticated AUTHORS.
- Royalty calculation logic is correctly applied end-to-end
  Database.
- Relationships between Author, Book, Publishing, and Sales are correctly handled


---
## 👉 Tests location

### Stack : 
- Jupiter Api Test
- SpringBootTest
### The tests are located in :
`PublishingMgt-master-back/src/test/java/com/publ/PublishingMgt_master`

- `e2e/RoyaltyE2ETest`
- `repositories/AuthorParticipationRepositoryTest`
- `repositories/MonthlySaleRepositoryTest`
- `repositories/PublishingRepositoryTest`
- `services/RoyaltyIntegrationTest`
- `services/RoyaltyServiceTest`

---


## 🚀 Run the tests
- Maven : `mvn clean test`

---
## 📛 Naming Convention
General format
- `should_<expectedBehavior>_when_<condition>`

or

- `given_<context>_when_<action>_then_<result>`

---

## 🧠 AuthorParticipationRepositoryTest

This test class verifies that the AuthorParticipationRepository correctly retrieves AuthorParticipation entities based on their associated Author or Book.

The tests are executed with Spring Boot's @DataJpaTest, which loads only the JPA components and uses an in-memory database to validate repository behavior in isolation.

### Overall Validation

It validates that the custom repository query methods:

- findByAuthor(Author author)
- findByBook(Book book)

work as expected and correctly retrieve AuthorParticipation entities based on their associations. They also indirectly verify the proper persistence and navigation of the relationships between Author, Book, and AuthorParticipation entities.

It confirms that:

- the repository correctly filters participations by book;
- the retrieved participation contains the expected associated author;
- JPA relationships are correctly persisted and loaded when querying by book

---

## 🌱 PublishingRepositoryTest

### What This Test Validates

✔ findByBook
- Verifies the Book → Publishing relationship
- Ensures that multiple editions/publications can be associated correctly
- Confirms the integrity of the JPA mapping

✔ findByIsbn

- Verifies a unique business lookup by ISBN
- Confirms proper handling of the Optional return type

✔ Negative case

- Verifies the repository's robustness when no matching data is found

--- 

## ⚡ MonthlySaleRepositoryTest

### What This Test Validates

✔ **`findByPublishingId` via Subselect/View Entity**

* Verifies that the `MonthlySale` entity correctly reads data from the underlying `BookSales` table through its subselect/view mapping.
* Ensures that sales records are properly exposed as `MonthlySale` objects.
* Confirms the integrity of the mapping between the database projection and the `MonthlySale` entity.

✔ **Data Mapping Accuracy**

* Validates that all expected fields are correctly populated:

  * Sale year
  * Sale month
  * Quantity sold
  * Quantity returned
  * Publishing ID
* Ensures that values stored in `BookSales` are accurately retrieved through the `MonthlySale` repository.

✔ **Filtering by Publishing**

* Verifies that `findByPublishingId(...)` correctly returns only the sales records associated with the specified publishing edition.
* Confirms that the repository query correctly filters data by `publishingId`.

✔ **Negative Case (No Sales Data)**

* Verifies that an empty result is returned when no sales records exist for a publishing edition.
* Confirms the repository's robustness when querying a valid publishing record with no associated sales.
* Ensures predictable behavior for empty datasets.

### Overall Validation

These tests validate that the `MonthlySaleRepository` correctly retrieves sales data through the `MonthlySale` projection/subselect entity, accurately maps data originating from `BookSales`, properly filters results by publishing edition, and safely handles cases where no sales data exists.


---

## 🧪 RoyaltyIntegrationTest

### What This Test Validates

✔ **End-to-End Royalty Calculation**

* Verifies the complete royalty calculation workflow from persisted data to the service result.

✔ **Entity Relationships**

* Confirms that the relationships between **Author**, **Book**, **AuthorParticipation**, **Publishing**, and **BookSales** are correctly used during the calculation process.

✔ **RoyaltyService Business Logic**

* Ensures that `RoyaltyService.getYearlyRoyaltiesByAuthor(...)` correctly aggregates sales and computes royalties for a given author and year.

✔ **Data Integration**

* Validates that data stored through the repositories is correctly retrieved and processed by the service layer.

✔ **Expected Royalty Amount**

* Confirms that the calculated royalty amount matches the expected business result (`18.0`) based on sales, returns, publishing royalty rate, and author participation rate.

### Overall Validation

This integration test validates the complete royalty calculation process, ensuring that repository persistence, entity relationships, and service-layer business rules work together correctly to produce the expected yearly royalty amount for an author.


---

## 🧾 RoyaltyServiceTest

### What This Test Validates

✔ **RoyaltyService Unit Logic (Isolated)**

* Verifies the business logic of `RoyaltyService` using mocked repositories.
* Ensures the service correctly computes yearly royalties without relying on the database.

✔ **Correct Aggregation Flow**

* Confirms that the service properly combines data from:

  * `AuthorParticipationRepository`
  * `PublishingRepository`
  * `MonthlySaleRepository`
* Validates the internal calculation pipeline for a single book scenario.

✔ **Year Filtering Logic**

* Ensures that only sales matching the requested year are included in the royalty calculation.
* Confirms that sales from other years are ignored.

✔ **DTO Mapping**

* Validates that the resulting `BookYearRoyaltyDTO` is correctly populated:

  * Book title
  * Year
  * Quantity sold
  * Total royalty amount

### Overall Validation

These tests validate the core behavior of `RoyaltyService`, ensuring correct royalty computation per author and year, proper integration of repository data (mocked), accurate filtering by year, and correct DTO output generation.


---
# 🐛 E2E TEST

 What this E2E test verifies

This test validates the complete end-to-end flow of yearly royalty calculation for an authenticated AUTHOR user, including authentication, database persistence, and API response.

🔁 Test flow (step by step)
### 1. User authentication context

The test simulates an AUTHOR user authenticated via Spring Security (JWT-based authentication in production).

### 2. Data setup in the database

### It creates a complete realistic dataset:

- A User (PubUser) with role AUTHOR
- A linked Author entity
- A Book
- An AuthorParticipation defining the author’s royalty percentage for the book
- A Publishing edition defining:
    - book price
    - publisher royalty rate

- A BookSales record representing sales data for year 2025:
    - quantity sold
    - quantity returned
    - discount

### 3. Business logic execution

The test calls the endpoint:

```
GET /api/royalties/mine/year/2025
```

This triggers:

- retrieval of the authenticated author
- aggregation of all books linked to the author
- calculation of yearly royalties per book using the business formula:
```
(quantity sold - returns)
× price
× (1 - discount)
× publishing royalty rate
× author participation rate
```

### 4. API response validation

The test verifies that:

- The response is HTTP 200 OK
- Exactly one book entry is returned
- The book title is correctly returned as "Spring E2E"

