/// <reference types="cypress" />

Cypress.Commands.add('login', (login: string, password: string) => {
  cy.visit('/login');

  cy.get('input[name="login"]').type(login);
  cy.get('input[name="password"]').type(password);

  cy.get('button[type="submit"]').click();

  cy.url().should('not.include', '/login');
});

Cypress.Commands.add('apiRequest', (options: Partial<Cypress.RequestOptions>) => {
  return cy.request({
    failOnStatusCode: false,
    ...options
  });
});

export {};