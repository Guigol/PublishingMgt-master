# 🟥 SKY ROYALTIES (Front-end) 🟦

##  Application purpose


- Complete UI Royalty rate calculation dashboards by author, book over a monthly or annual period.
- Admin, Author, User and manager views (4 roles).
- UI charts overviews.
- Download xls files for author's participation royalties.
- Search bars for books, authors, publishing, ids...
- Management for books, Sales, users, author's participation, publishers, publishing's...

---

## 📋 Table of contents

- [🏛️ Structure](#-structure)
- [🛠️ Technologies](#-technologies)
- [🚀 Run the application](#-run-the-application)
- [🔑 Login](#-login)
- [🎉 KPI CARDS](#-kpi-cards-)
- [🐞 Jasmine Unit tests](#-jasmine-unit-tests)- 
- [🔍 Cypress E2E tests](#-cypress-e2e-tests)



---

## 🏛️ Structure

```
src/
├── app/
│   ├── core/                     
│   │   ├── interceptors/
│   │   │      └── jwt.interceptor.ts
│   │   │
│   │   ├── guards/
│   │   │     ├── auth.guard.ts
│   │   │     ├── author.guard.ts
│   │   │     ├── author.guard.spec.ts
│   │   │     └── ...
│   │   │     
│   │   ├── services/
│   │   │     ├── auth.service.ts
│   │   │     ├── book.service.ts
│   │   │     └── ...
│   │   │
│   │   └── models/
│   │         ├── royalty.model.ts
│   │         ├── book.model.ts
│   │         ├── book-sale.model.ts
│   │         └── ...
│   │
│   ├── shared/                   
│   │   ├── footer/
│   │   │   ├── footer.component.ts
│   │   │   └── footer.component.html
│   │   │            
│   │   └── navbar/
│   │       └── ...
│   │
│   ├── features/                 
│   │   ├── auth/
│   │   │   ├── login/
│   │   │   │   ├── login.component.ts
│   │   │   │   └── login.component.html
│   │   │   └── auth.routes.ts
│   │   │
│   │   ├── admin/
│   │   │   
│   │   ├── royalties/
│   │   │ 
│   │   ├── manager/   
│   │   │   
│   │   ├── overview/  
│   │   │   
│   │   └── dashboard/
│   │
│   ├── pages/
│   │       
│   ├── testing/
│   │
│   ├── app.routes.ts             
│   ├── app.component.ts
│   └── app.html
│
├── assets/
│     └── img/
│
├── environments/
│   ├── environment.ts
│   └── environment.prod.ts
│
├── test-setup.ts
├── material-theme.scss
└── main.ts

```

## 🛠️ Technologies

- Angular : 21.1.5
- Material UI : 21.1.5
- TypeScript : 5.9.2
- cypress (e2e test) : 15.17.0
- jasmine-core (unit tests) : 6.3.0


---
## 🚀 Run the application

**Once the backend is started, to build the project run :**

```bash
ng build
```
**or**
```bash
npm install
```

**and then to start a local development server, run :**
```bash
ng serve
```
**Once the server is running, open your browser and navigate to :**

`http://localhost:4200/`

---
## 🔑 Login

**Admin** Role

```
login : admin
password : admin123
```
**Manager** role

```
login : mgr1
password : 12345
```
**User** role

```
login : useruser
password : 12345
```
**Author** role

```
login : vhugo
password : 12345
```
Or any other login from the authors table (jelroy, cdesegur, acamus...)


---


## 🎉 KPI CARDS 

[ KPI CARDS ]
- Total royalties per year
- Top 1 book
- Monthly average
- Number of active books

[ CHART 1 ]
📊 Bar chart : royalties per book

[ CHART 2 ]
📈 Line chart : monthly trend

[ TABLE DETAIL (optional) ]
drill-down (clic on one book)

---

## 🐞 Jasmine Unit tests

These tests are meant to check the robustness of some components for UI action attended on the front-end.

```bash
ng test
```
**To run one test in particular :**
```bash
ng test --include="src/app/core/services/royalties.service.spec.ts"
```

**Stop the tests**
```bash
ctrl + c
```

---

## 🔍 Cypress E2E tests

These tests verify the complete flow between the back (Java Spring) and Angular front-end.

### Structure / Location
```

├── PublishingMgt-master-front/
│
├── src/
│                    
├── cypress/
│       └── e2e/
│       │    └── access-control-matrix.cy.ts
│       │    └── author-login-real.cy.ts
│       │    └── royalties-data-consistency.cy.ts
        │
        ├── fixtures/
        │     └── users.json
        │   
        ├── support/
        │   ├── auth.helper.ts
        │   ├── commands.ts
        │   ├── cypress.d.ts
        │   └── e2e.ts
        │
        └── tsconfig.json

```

### Run Cypress E2E tests

```bash
npx cypress run
```
**To run one test in particular :**
```bash
npx cypress run --spec "cypress/e2e/access-control-matrix.cy.ts"
```
---




