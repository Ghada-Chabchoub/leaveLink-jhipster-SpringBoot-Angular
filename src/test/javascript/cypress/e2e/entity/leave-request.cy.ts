import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('LeaveRequest e2e test', () => {
  const leaveRequestPageUrl = '/leave-request';
  const leaveRequestPageUrlPattern = new RegExp('/leave-request(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const leaveRequestSample = {
    title: 'ah',
    fromDate: '2024-07-19',
    toDate: '2024-07-19',
    status: 'REQUESTED',
    department: 'MARKETING',
    changedAt: '2024-07-19T17:28:05.503Z',
  };

  let leaveRequest;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/leave-requests+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/leave-requests').as('postEntityRequest');
    cy.intercept('DELETE', '/api/leave-requests/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (leaveRequest) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/leave-requests/${leaveRequest.id}`,
      }).then(() => {
        leaveRequest = undefined;
      });
    }
  });

  it('LeaveRequests menu should load LeaveRequests page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('leave-request');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LeaveRequest').should('exist');
    cy.url().should('match', leaveRequestPageUrlPattern);
  });

  describe('LeaveRequest page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(leaveRequestPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LeaveRequest page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/leave-request/new$'));
        cy.getEntityCreateUpdateHeading('LeaveRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveRequestPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/leave-requests',
          body: leaveRequestSample,
        }).then(({ body }) => {
          leaveRequest = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/leave-requests+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/leave-requests?page=0&size=20>; rel="last",<http://localhost/api/leave-requests?page=0&size=20>; rel="first"',
              },
              body: [leaveRequest],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(leaveRequestPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details LeaveRequest page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('leaveRequest');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveRequestPageUrlPattern);
      });

      it('edit button click should load edit LeaveRequest page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LeaveRequest');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveRequestPageUrlPattern);
      });

      it('edit button click should load edit LeaveRequest page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LeaveRequest');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveRequestPageUrlPattern);
      });

      it('last delete button click should delete instance of LeaveRequest', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('leaveRequest').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveRequestPageUrlPattern);

        leaveRequest = undefined;
      });
    });
  });

  describe('new LeaveRequest page', () => {
    beforeEach(() => {
      cy.visit(`${leaveRequestPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LeaveRequest');
    });

    it('should create an instance of LeaveRequest', () => {
      cy.get(`[data-cy="title"]`).type('prairie');
      cy.get(`[data-cy="title"]`).should('have.value', 'prairie');

      cy.get(`[data-cy="description"]`).type('jasmine mmm');
      cy.get(`[data-cy="description"]`).should('have.value', 'jasmine mmm');

      cy.get(`[data-cy="fromDate"]`).type('2024-07-19');
      cy.get(`[data-cy="fromDate"]`).blur();
      cy.get(`[data-cy="fromDate"]`).should('have.value', '2024-07-19');

      cy.get(`[data-cy="toDate"]`).type('2024-07-19');
      cy.get(`[data-cy="toDate"]`).blur();
      cy.get(`[data-cy="toDate"]`).should('have.value', '2024-07-19');

      cy.get(`[data-cy="status"]`).select('APPROVED');

      cy.get(`[data-cy="department"]`).select('FINANCE');

      cy.get(`[data-cy="changedAt"]`).type('2024-07-19T11:59');
      cy.get(`[data-cy="changedAt"]`).blur();
      cy.get(`[data-cy="changedAt"]`).should('have.value', '2024-07-19T11:59');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        leaveRequest = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', leaveRequestPageUrlPattern);
    });
  });
});
