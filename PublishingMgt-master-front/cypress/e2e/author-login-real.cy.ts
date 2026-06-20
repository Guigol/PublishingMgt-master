/// <reference types="cypress" />

import users from '../fixtures/users.json';

describe('PublishingMgt - AUTHOR REAL JWT (Victor Hugo)', () => {

  const api = Cypress.env('apiUrl');

  function loginAsAuthor() {

    cy.login(
      users.author.login,
      users.author.password
    );

    cy.url().should('include', '/author-overview');
  }

  // =========================
  // TEST 1 - LOGIN
  // =========================
  it('login + redirect dashboard', () => {

    loginAsAuthor();

  });

  // =========================
  // TEST 2 - /me
  // =========================
  it('should retrieve user via /me', () => {

    loginAsAuthor();

    cy.request({
      method: 'GET',
      url: `${api}/api/auth/me`,
      failOnStatusCode: false
    }).then((res: any) => {

      expect(res.status).to.eq(200);
      expect(res.body.login).to.eq(users.author.login);
      expect(res.body.role).to.eq(users.author.role);

    });

  });

  // =========================
  // TEST 3 - ROYALTIES
  // =========================
    it('should access royalties secured endpoint', () => {

      loginAsAuthor();

      cy.request({
        method: 'GET',
        url: `${api}/api/royalties/mine/year/2025`,
        failOnStatusCode: false
      }).then((res: any) => {

        expect(res.status).to.eq(200);

      });

    });

});