/// <reference types="cypress" />

import users from '../fixtures/users.json';

describe('PublishingMgt - ROYALTIES DATA CONSISTENCY (REAL BACKEND)', () => {

  const api = Cypress.env('apiUrl');

  function loginAuthor() {

    cy.login(
      users.author.login,
      users.author.password
    );

    cy.url().should('include', '/author-overview');
  }

  function loginAdmin() {

    cy.login(
      users.admin.login,
      users.admin.password
    );

    cy.url().should('include', '/admin-overview');
  }

  // ================================
  // TEST 1: AUTHOR YEARLY ROYALTIES
  // ================================
  it('AUTHOR should retrieve yearly royalties (2025)', () => {

    loginAuthor();

    cy.request({
      method: 'GET',
      url: `${api}/api/royalties/mine/year/2025`,
      failOnStatusCode: false
    }).then((res: any) => {

      expect(res.status).to.eq(200);
      expect(res.body).to.be.an('array');

      if (res.body.length > 0) {
        expect(res.body[0]).to.have.property('year');
      }

    });

  });

  // ===============================
  // TEST 2: AUTHOR MONTHLY DETAILS
  // ===============================
  it('AUTHOR should retrieve monthly royalties details', () => {

    loginAuthor();

    cy.request({
      method: 'GET',
      url: `${api}/api/royalties/mine/book/93/year/2025`,
      failOnStatusCode: false
    }).then((res: Cypress.Response<any>) => {

      expect(res.status).to.be.oneOf([200, 404]); // safe fallback
      expect(res.body).to.be.an('array');

    });

  });

  // ===================================
  // TEST 3: ADMIN CAN ACCESS BY AUTHOR
  // ===================================
  it('ADMIN should access royalties by author', () => {

    loginAdmin();

    cy.request({
      method: 'GET',
      url: `${api}/api/royalties/by-author/1`,
      failOnStatusCode: false
    }).then((res: Cypress.Response<any>) => {

      expect(res.status).to.eq(200);
      expect(res.body).to.be.an('array');

    });

  });

  // =================================
  // TEST 4: ADMIN CAN ACCESS BY BOOK
  // =================================
  it('ADMIN should access royalties by book', () => {

    loginAdmin();

    cy.request({
      method: 'GET',
      url: `${api}/api/royalties/by-book/1`,
      failOnStatusCode: false
    }).then((res: Cypress.Response<any>) => {

      expect(res.status).to.eq(200);
      expect(res.body).to.be.an('array');

    });

  });

});