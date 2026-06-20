# 🌱 SKY ROYALTIES (Back-end) ☕

##  Application purpose 


- Royalty rate calculation by author, book over a monthly or annual period.
- Admin, Author, User and manager views (4 roles).
- Json data loader for each entity.
- Secure Jwt HttpCookie's only authentication.
- Complete CRUD: books, Sales, users, author's participation, publishers, publishing's...
---

## 📋 Table of contents

- [🌐 Technologies](#-Technologies)
- [🎯 Run the application](#-run-the-application-)
- [📊 H2 connexion](#-h2-connexion)
- [🗃️ Data loader](#-data-loader)
- [💰 ROYALTIES MANAGEMENT](#-royalties-management)
- [🔌API](#api--)
- [⚙️ CRUD](#-crud)
- [💶 SALES Accountability](#-sales-accountability)
- [👤 User journey](#-user-journey)
- [🔍 Endpoints rules](#-endpoints-rules)
- [📘 Royalty Calculation Rule (Summary)](#-royalty-calculation-rule-summary)
    - [🧪 Formula](#-formula)
    - [🧠 Meaning of each component](#-meaning-of-each-component)
    - [📌 Conceptual model](#-conceptual-model)
    - [💡 Interpretation](#-interpretation)
- [📝 Disclaimer](#-disclaimer)

---

## 🌐 Technologies

- Java 17
- Springboot 3.5
- Hibernate
- Maven
- REST API
- Jwt Authentication (HttpCookieOnly)
- H2 console
- MySql
- json
---

## 🎯 Run the application :
- Maven : `mvn spring-boot:run`
---

## 📊 H2 connexion

`http://localhost:8088/h2-console/`

- JDBC URL : `jdbc:h2:mem:publmgt?createDatabaseIfNotExist=true`
- User Name : `sa`
- Password : `sa`

---
## 🗃️ Data loader

👉 At the start of Spring application, a `CommandLineRunner` located in 

`PublishingMgt-master-back/src/main/java/com/publ/PublishingMgt_master/config/DataLoader.java`

loads json data for each entity. The json file **seeData.json** is located in :

`PublishingMgt-master-back/src/main/resources/seeData.json`

---
# 💰 ROYALTIES MANAGEMENT

### Logged as an author, the author's own royalties

- My royalties : `GET http://localhost:8088/api/royalties/mine`

### Logged as an author, royalties per year

- My royalties/year : `GET http://localhost:8088/api/royalties/mine/year/{year}`

### Logged as an author, royalties per month

- My royalties/year/book/month : `GET http://localhost:8088/api/royalties/mine/book/{book_name}/year/{year}`
 

### Logged as Manager or Admin

- Get royalties by author Id : `GET http://localhost:8088/api/royalties/by-author/{authorId}`

### Logged as Manager or Admin

- Get royalties by book Id : `GET http://localhost:8088/api/royalties/by-book/{bookId}`

### Logged as User
The User can only view book sales and search by book, author, Id...

- Get all book-sales : `GET http://localhost:8088/api/book-sales/all`

---


# 🔌API : 
Roles : MANAGER, USER, AUTHOR, ADMIN

- signin :`POST http://localhost:8088/api/auth/login`

- signup :`POST http://localhost:8088/api/auth/signup`

### User's login
 ```
  {
  "login": "useruser",
  "password": "12345"
  }
  ```

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
  "password": "12345"
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
# ⚙️ CRUD

## 🛡️ Logged as Admin : CRUD Users

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

### Role : ADMIN, USER, MANAGER
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
### Role : AUTHOR
```
{
  "login": "mhouellebeq",
  "password": "12345",
  "role": "AUTHOR",
  "author": {
    "firstname": "Michel",
    "surname": "Houellebeq"
  }
}

```
---
- Get Number Of Users `GET http://localhost:8088/api/tools/numberOfUsers`
---


## 🛡️ Logged as Manager or Admin : CRUD Books
- List of All Books `GET http://localhost:8088/api/tools/book`
---

- Delete Book By Id `DELETE http://localhost:8088/api/tools/book/{id}`

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
- Update Book By Id `PUT http://localhost:8088/api/tools/book/{id}`

```
{
  "title": "Salambo COR",
  "publisherId": 1,
  "authorIds": [7]
}

```
---
# 💶 SALES accountability

### Logged as Manager or Admin or User (not Author)

- List of All Sales `GET http://localhost:8088/api/book-sales/all`
---

- Sales By Book Id `GET http://localhost:8088/api/book-sales/book/{bookId}`

---
- Create Sale `POST http://localhost:8088/api/book-sales/add`
```
{
  "year": 2026,
  "month": 1,
  "quantitySold": 138,
  "quantityReturn": 5,
  "averageDiscount": 0.1,
  "publishing": {
    "isbn": "9782077360036"
  }
}
```
---
- Update Sale By Id `PUT http://localhost:8088/api/book-sales/update/{id}`
```
{
  "year": 2026,
  "month": 1,
  "quantitySold": 200,
  "quantityReturn": 5,
  "averageDiscount": 0.1,
  "publishing": {
    "isbn": "9782077360036"
  }
}

```
---
- Delete Sale By Id `DELETE http://localhost:8088/api/book-sales/delete/{id}`
---

### See controllers for Authors, Publishing, Publisher, Author's participation...'s CRUD

---

# 👤 User journey
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

# 🔍 Endpoints rules

 Endpoint               | AUTHOR | MANAGER | ADMIN | USER |
|------------------------| ------ | ------- | ----- |------|
| `/api/auth/**`         | ✅      | ✅       | ✅     | ✅    |
| `/api/h2-console/**`   | ✅      | ✅       | ✅     | ✅    |
| `/api/royalties/mine`  | ✅      | ❌       | ❌     | ❌    |
| `/api/book-sales/mine` | ✅      | ❌       | ❌     | ❌    |
| `/api/royalties/**`    | ❌      | ✅       | ✅     | ❌    |
| `/api/book-sales/**`   | ❌      | ✅       | ✅     | ❌    |
| `/api/book-sales/all`  | ❌      | ✅       | ✅     | ✅    |
| `/api/tools/pubuser`  | ❌      | ❌       | ✅     | ❌    |

---

# 📘 Royalty Calculation Rule (Summary)

👉 MonthlySale is a replica of Booksales for the authors' session. Royalties are calculated from the MonthlySale SQL view.

### BookSales = Single source of truth


Royalties are calculated in two steps:

1. A global royalty pool is first created from book sales.
2. This pool is then distributed to authors based on their participation rate.

## 🧪 Formula
```
royalty =
netQuantity
× unitPrice
× (1 - discount)
× pctRoyalties
× pctPart
```
## 🧠 Meaning of each component
- netQuantity: sold quantity minus returned quantity
- unitPrice: book price excluding tax (HT)
- discount: average discount applied to sales
- pctRoyalties: global royalty rate defined at book level (share of revenue allocated to royalties)
- pctPart: author's share of the royalty pool for the book

## 📌 Conceptual model
- pctRoyalties defines the size of the royalty pool
- pctPart defines the author’s share of that pool

## 💡 Interpretation

First, the system calculates the revenue generated by sales after discounts.
Then, a percentage of this revenue is allocated to royalties (pctRoyalties).
Finally, this royalty pool is split between authors according to their participation rate (pctPart).
---
# 📝 Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.