describe('Lang gjennomgang', () => {
  it('Velger første alternativ på alt, og fullfør', () => {
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
        cy.get('input').eq(0).should('have.value', 'Ja');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 2);
    cy.get('[class="Skjemafelt"]')
      .eq(1)
      .within(() => {
        cy.get('input').should('have.length', 2);
        cy.get('input').eq(0).should('have.value', 'Ja');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 3);
    cy.get('[class="Skjemafelt"]')
      .eq(2)
      .within(() => {
        cy.get('[class$=-container]').eq(0).click();
        cy.get('[class$=-option]').should('have.length', 500);
        cy.get('[class$=-option]').eq(0).click();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 4);
    cy.get('[class="Skjemafelt"]')
      .eq(3)
      .within(() => {
        cy.get('input').should('have.length', 2);
        cy.get('input').eq(0).should('have.value', 'Gjelder én ytelse eller tjeneste');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 5);
    cy.get('[class="Skjemafelt"]')
      .eq(4)
      .within(() => {
        cy.get('textarea').should('have.length', 1);
        cy.get('textarea').eq(0).type('Kort beskrivelse');
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 6);
    cy.get('[class="Skjemafelt"]')
      .eq(5)
      .within(() => {
        cy.get('select').select(1).should('have.value', 'AFP - Avtalefestet pensjon');
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 8);
    cy.get('[class="Skjemafelt"]')
      .eq(6)
      .within(() => {
        cy.get('input').should('have.length', 5);
        cy.get('input').eq(0).should('have.id', 'Korona-saken');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 8);
    cy.get('[class="Skjemafelt"]')
      .eq(7)
      .within(() => {
        cy.get('input').should('have.length', 7);
        cy.get('input').eq(0).should('have.value', 'Vente på NAV');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 9);
    cy.get('[class="Skjemafelt"]')
      .eq(8)
      .within(() => {
        cy.get('input').should('have.length', 6);
        cy.get('input').eq(0).should('have.value', 'Saksbehandlingstid');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 10);
    cy.get('[class="Skjemafelt"]')
      .eq(9)
      .within(() => {
        cy.get('input').should('have.length', 4);
        cy.get('input')
          .eq(0)
          .should('have.value', 'a) Regler/rutiner/frister er fulgt - NAV har ivaretatt bruker godt');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="Skjemafelt"]').should('have.length', 11);
    cy.get('[class="Skjemafelt"]')
      .eq(10)
      .within(() => {
        cy.get('input').should('have.length', 2);
        cy.get('input').eq(0).should('have.value', 'Ja');
        cy.get('input').eq(0).check();
      });

    cy.get('[class="SubmitButton"]').should('have.length', 1);
    cy.get('[class="SubmitButton"]')
      .eq(0)
      .within(() => {
        cy.get('button').should('have.length', 1);
        cy.get('button').eq(0).click();
      });

    cy.fixture('klassifiser/forsteValg.json').then((body) => {
      cy.wait('@klassifiser').then((interception) => {
        const requestBody = interception.request.body;
        expect(requestBody).to.deep.equal(body);
      });
    });
  });
});
