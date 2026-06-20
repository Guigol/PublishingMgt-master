/// <reference types="cypress" />

import users from '../fixtures/users.json';

describe('PublishingMgt - ACCESS CONTROL MATRIX (REAL BACKEND)', () => {

  const api = Cypress.env('apiUrl');

  // TEST 1
  it('USER should access book-sales API (200)', () => {

    cy.login(
      users.user.login,
      users.user.password
    );

    cy.apiRequest({
      url: `${api}/api/book-sales/all`
    }).then(res => {

      expect(res.status).to.eq(200);

    });

  });

  // TEST 2
  it('ADMIN should access book-sales API (200)', () => {

    cy.login(
      users.admin.login,
      users.admin.password
    );

    cy.apiRequest({
      url: `${api}/api/book-sales/all`
    }).then(res => {

      expect(res.status).to.eq(200);

    });

  });

  // TEST 3
  it('AUTHOR should be forbidden (403)', () => {

    cy.login(
      users.author.login,
      users.author.password
    );

    cy.apiRequest({
      url: `${api}/api/book-sales/all`
    }).then(res => {

      expect(res.status).to.eq(403);

    });

  });

  // TEST 4
  it('MANAGER should access book-sales API (200)', () => {

    cy.login(
      users.manager.login,
      users.manager.password
    );

    cy.apiRequest({
      url: `${api}/api/book-sales/all`
    }).then(res => {

      expect(res.status).to.eq(200);

    });

  });

  // TEST 5
  it('MANAGER should be forbidden (403)', () => {

    cy.login(
      users.manager.login,
      users.manager.password
    );

    cy.apiRequest({
      url: `${api}/api/tools/pubuser`
    }).then(res => {

      expect(res.status).to.eq(403);

    });

  });

});