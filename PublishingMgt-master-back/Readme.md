# Application purpose

Royalty rate calculation by author, book over a monthly or annual period. Author and manager view. Complete CRUD: books, Sales, users, publishers, edition (collection)...

## Technologies

- H2 console
- Java 17
- Springboot 3.5
- Jwt Authentication
- json


# Run the application :
- Maven : `mvn spring-boot:run`

## H2 connexion

`http://localhost:8088/h2-console/`

- JDBC URL : `jdbc:h2:mem:publmgt?createDatabaseIfNotExist=true`
- User Name : `sa`
- Password : `sa`

---
# ROYALTIES MANAGEMENT

## Logged as an author, the author's own royalties

- My royalties `GET http://localhost:8088/api/royalties/mine`
--- 
## Logged as Manager or Admin

- Get royalties by author Id `GET http://localhost:8088/api/royalties/by-author/{authorId}`
---
## Logged as Manager or Admin

- Get royalties by book Id `GET http://localhost:8088/api/royalties/by-book/{bookId}`

---

# API : 
Roles : MANAGER, USER, AUTHOR, ADMIN

- signin :`POST http://localhost:8088/api/auth/login`

- signup :`POST http://localhost:8088/api/auth/signup`
 ```
  {
  "login": "Coco",
  "password": "12345"
  }
  ```
---


### Manager's login
  ```
  {
  "login": "mgr1",
  "password": "12345"
  }
  ```

### Author's  login
  ```
  {
  "login": "vhugo",
  "password": "causette"
  }
  ```
### Admin's login
  ```
  {
  "login": "admin",
  "password": "admin123"
  }
  ```
---
## Logged as Admin : CRUD Users

- Create User `POST http://localhost:8088/api/tools/pubuser`
```
{
  "login": "gflaubert",
  "password": "monMdpSecret",
  "role": "AUTHOR",
  "author": {
    "firstname": "Gustave",
    "surname": "Flaubert"
  }
}
```
---
- Get All Users `GET http://localhost:8088/api/tools/pubuser`
---

- Get User By Id `GET http://localhost:8088/api/tools/pubuser/{id}`
---

- Delete User By Id `DELETE http://localhost:8088/api/tools/pubuser/{id}`

---
- Update User By Id `PUT http://localhost:8088/api/tools/pubuser/{id}`
```
{
  "login": "jdoe",
  
  "role": "MANAGER",
  "author": {
    "firstname": "",
    "surname": ""
  }
}
```
---
- Get Number Of Users `GET http://localhost:8088/api/tools/numberOfUsers`
---

# BOOKS

## Logged as Manager or Admin : CRUD Books
- List of All Books `GET http://localhost:8088/api/tools/book`
---

- Delete Book By Id `DELETE http://localhost:8088/api/tools/book/{id}`

---
- Update Book By Id `PUT http://localhost:8088/api/tools/book/{id}`
---
- Create Book `POST http://localhost:8088/api/tools/book`
```
{
  "title": "Salambo",
  "publisherId": 1,
  "authorIds": [7]
}

```
---
# SALES accountability

## Logged as Manager or Admin 

- List of All Sales `GET http://localhost:8088/api/book-sales/all`
---

- Sales By Book Id `GET http://localhost:8088/api/book-sales/book/{bookId}`

---
- Update Sale By Id `PUT http://localhost:8088/api/book-sales/update/{id}`
```
{
  "year": 2024,
  "month": 2,
  "quantitySold": 55,
  "quantityReturn": 2,
  "averageDiscount": 0.1,
  "book": {
    "book_id": 5
  },
  "publishing": {
    "isbn": "9782077360036"
  }
}

```
---
- Delete Sale By Id `DELETE http://localhost:8088/api/book-sales/delete/{id}`
---
- Create Sale `POST http://localhost:8088/api/book-sales/add`
```
{
  "year": 2025,
  "month": 8,
  "quantitySold": 120,
  "quantityReturn": 5,
  "averageDiscount": 0.1,
  "book": {
    "book_id": 6
  },
  "publishing": {
    "isbn": "9782077560036"
  }
}
```
### See controllers for Publishing, Publisher's CRUD

---

# User's journey
```
[user login]
|
v
[AuthService] -- retrieve PubUser and the associated author if any
|
v
+-----------------------------+
|       RoyaltyController     |
+-----------------------------+
| /api/royalties/mine         | <-- Accessible only to AUTHOR
|   -> call getRoyaltiesByAuthor(author) 
|                             |
| /api/royalties/by-author/{authorId}  <-- Accessible to MANAGER and ADMIN
|   -> call getRoyaltiesByAuthorId(authorId) 
|                             |
| /api/royalties/by-book/{bookId} <-- Accessible to MANAGER and ADMIN
|   -> call getRoyaltiesByBook(bookId)
+-----------------------------+
|
v
[RoyaltyService]
- Retrieve AuthorParticipation from DB
- Retrieve Publishing linked to Book
- Calculate Royalties amount  = priceNoTax * taux_royalties_publishing * pctPartAuthor
- Build AuthorRoyaltyDTO (title, amount, month, year)
  |
  v
  [Return JSON through ObjectMapper.valueToTree()]
  |
  v
  [Front / API client] displays royalties
```

# Endpoints rules

Endpoint               | AUTHOR | MANAGER | ADMIN |
| ---------------------- | ------ | ------- | ----- |
| `/api/auth/**`         | ✅      | ✅       | ✅     |
| `/api/h2-console/**`   | ✅      | ✅       | ✅     |
| `/api/royalties/mine`  | ✅      | ❌       | ❌     |
| `/api/book-sales/mine` | ✅      | ❌       | ❌     |
| `/api/royalties/**`    | ❌      | ✅       | ✅     |
| `/api/book-sales/**`   | ❌      | ✅       | ✅     |

