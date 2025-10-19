```[Utilisateur se connecte]
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
Endpoint               | AUTHOR | MANAGER | ADMIN |
| ---------------------- | ------ | ------- | ----- |
| `/api/auth/**`         | ✅      | ✅       | ✅     |
| `/api/h2-console/**`   | ✅      | ✅       | ✅     |
| `/api/royalties/mine`  | ✅      | ❌       | ❌     |
| `/api/book-sales/mine` | ✅      | ❌       | ❌     |
| `/api/royalties/**`    | ❌      | ✅       | ✅     |
| `/api/book-sales/**`   | ❌      | ✅       | ✅     |

