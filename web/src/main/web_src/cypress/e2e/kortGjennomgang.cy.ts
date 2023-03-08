describe('Kort gjennomgang', () => {
  it('Velger nei på alt, og fullfør', () => {
    cy.intercept('/rest/taskserviceklage/autentisert', {
      statusCode: 200,
    });
    cy.intercept('/rest/taskserviceklage/hentdokument/test', {
      statusCode: 200,
      fixture: 'hentDokument/lang.json',
    });
    cy.intercept('/rest/taskserviceklage/hentskjema/test', {
      statusCode: 200,
      fixture: 'hentSkjema.json',
    });
    cy.intercept('PUT', '/rest/taskserviceklage/klassifiser?oppgaveId=test', {
      statusCode: 200,
    }).as('klassifiser');
    cy.visit('/serviceklage/klassifiser?oppgaveId=test');

    cy.get('[class="Feilmelding"]').should('not.exist');

    cy.get('[class="Skjemafelt"]').should('have.length', 1);
    cy.get('[class="Skjemafelt"]')
      .eq(0)
      .within(() => {
        cy.get('input').should('have.length', 7);
        cy.get('input').eq(6).should('have.value', 'Nei, annet');
        cy.get('input').eq(6).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 2);

    cy.get('[class="Skjemafelt"]')
      .eq(1)
      .within(() => {
        cy.get('input').should('have.length', 2);
        cy.get('input').eq(1).should('have.value', 'Nei');
        cy.get('input').eq(1).check();
      });

    cy.get('[class="SubmitButton"]').should('have.length', 1);
    cy.get('[class="SubmitButton"]')
      .eq(0)
      .within(() => {
        cy.get('button').should('have.length', 1);
        cy.get('button').eq(0).click();
      });

    cy.fixture('klassifiser/neiTilAlt.json').then((body) => {
      cy.wait('@klassifiser', { timeout: 100000 }).then((interception) => {
        const requestBody = interception.request.body;
        expect(requestBody).to.deep.equal(body);
      });
    });
  });
});
