/// <reference types="cypress" />

declare global {
  namespace Cypress {
    interface Chainable {
      login(login: string, password: string, redirect?: string): Chainable<void>;
      apiRequest(options: Partial<Cypress.RequestOptions>): Chainable<Cypress.Response<any>>;
    }
  }
}

export {};