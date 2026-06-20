export function loginByApi(user: string, pass: string, api: string) {

  return cy.request({
    method: 'POST',
    url: `${api}/api/auth/login`,
    body: {
      login: user,
      password: pass
    }
  });
}