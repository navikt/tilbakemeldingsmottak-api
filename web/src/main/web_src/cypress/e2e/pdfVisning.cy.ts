describe('Visning av PDF', () => {
  beforeEach(() => {
    cy.intercept('/rest/taskserviceklage/autentisert', {
      statusCode: 200,
    });
    cy.intercept('/rest/taskserviceklage/hentskjema/test', {
      statusCode: 200,
      fixture: 'hentSkjema.json',
    });
  });
  it('sjekker om neste/forrige-knapp ikke vises', () => {
    cy.intercept('/rest/taskserviceklage/hentdokument/test', {
      statusCode: 200,
      fixture: 'hentDokument/kort.json',
    });
    cy.visit('/serviceklage/klassifiser?oppgaveId=test');

    cy.get('[class="Dokument"]').should('have.length', 1);
    cy.get('[class="Dokument"]')
      .eq(0)
      .within(() => {
        cy.get('button').should('have.length', 1);
      });
  });

  it('sjekker om neste/forrige-knapp vises', () => {
    cy.intercept('/rest/taskserviceklage/hentdokument/test', {
      statusCode: 200,
      fixture: 'hentDokument/lang.json',
    });
    cy.visit('/serviceklage/klassifiser?oppgaveId=test');

    cy.get('[class="Dokument"]').should('have.length', 1);
    cy.get('[class="Dokument"]')
      .eq(0)
      .within(() => {
        cy.get('button').should('have.length', 3);
        cy.get('button').eq(1).should('have.text', 'Forrige');
        cy.get('button').eq(2).should('have.text', 'Neste');
      });
  });
});
