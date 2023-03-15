describe('Dokument mangler', () => {
  it('Viser tekst for dokumentfeil', () => {
    cy.intercept('/rest/taskserviceklage/autentisert', {
      statusCode: 200,
    });
    cy.intercept('/rest/taskserviceklage/hentdokument/test', {
      statusCode: 204,
    });
    cy.visit('/serviceklage/klassifiser?oppgaveId=test');
    cy.get('[class="Dokumentfeil"]').should('have.length', 1);
    cy.get('[class="Feilmelding"]').should('not.exist');
  });
});
