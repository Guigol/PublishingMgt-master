# Application purpose

Royalty rate calculation by author, book over a monthly or annual period. Author and manager view. Complete CRUD: books, users, publishers, edition (collection)...

## Technologies

- H2 console
- Java 17
- Springboot 3.5
- Jwt Authentication


# Run the application :
- Maven : `mvn spring-boot:run`

## H2 connexion

`http://localhost:8088/h2-console/`

- JDBC URL : `jdbc:h2:mem:publmgt?createDatabaseIfNotExist=true`
- User Name : `sa`
- Password : `sa`


## Api login


- `curl -X POST http://localhost:8088/api/auth/login
- Roles : MANAGER, USER, AUTHOR, ADMIN

### Manager
  ```{
  "login": "mgr1",
  "password": "12345"
  }
  ```

### Author
  ```{
  "login": "vhugo",
  "password": "causette"
  }
  ```
### Admin
  ```{
  "login": "admin",
  "password": "admin123"
  }
  ```

- `curl -X GET http://localhost:8088/api/tools/book
- `curl -X GET http://localhost:8088/api/tools/pubuser



# User's journey
```
[Utilisateur se connecte]
|
v
[AuthService] -- récupère PubUser et éventuellement l'Author associé
|
v
+-----------------------------+
|       RoyaltyController     |
+-----------------------------+
| /api/royalties/mine         | <-- Accessible uniquement aux AUTHOR
|   -> appelle getRoyaltiesByAuthor(author) |
|                             |
| /api/royalties/by-author/{authorId}     | <-- Accessible MANAGER et ADMIN
|   -> appelle getRoyaltiesByAuthorId(authorId) |
|                             |
| /api/royalties/by-book/{bookId}        | <-- Accessible MANAGER et ADMIN
|   -> appelle getRoyaltiesByBook(bookId) |
+-----------------------------+
|
v
[RoyaltyService]
- Récupère AuthorParticipation depuis DB
- Récupère Publishing lié au Book
- Calcule montant = prixHT * taux_royalties_publishing * pctPartAuthor
- Construit AuthorRoyaltyDTO (titre, montant, mois, année)
  |
  v
  [Retour JSON via ObjectMapper.valueToTree()]
  |
  v
  [Front / API client] affiche les redevances
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

