# Repository overview

This repository contains **Tilbakemeldingsmottak**, a **Kotlin + Spring Boot** backend for NAV's feedback intake system.

It handles:

- **service complaints** from external users
- **bug / missing-feature reports**
- **praise**
- **requests for Sami-language conversations**
- **caseworker classification** of submitted service complaints

## Top-level structure

| Path | Purpose |
| --- | --- |
| `app/` | Main application: controllers, services, security, database access, and external integrations |
| `api/` | OpenAPI specification and generated Kotlin/Spring API interfaces and models |
| `pdfutility/` | Shared PDF generation and validation utilities |
| `stubs/` | WireMock stubs for local development against external systems |
| `.nais/` | Deployment configuration for NAIS |
| `.github/workflows/` | CI/CD workflows |

## Main application

The main runtime module is **`app/`**. Its entrypoint is:

- `app/src/main/kotlin/no/nav/tilbakemeldingsmottak/Application.kt`

The application exposes endpoints for:

- `POST /rest/serviceklage` — submit a service complaint
- `PUT /rest/taskserviceklage/klassifiser` — classify a complaint as a caseworker
- `GET /rest/taskserviceklage/hentskjema/{oppgaveId}` — fetch the submitted complaint form
- `GET /rest/taskserviceklage/hentdokument/{oppgaveId}` — fetch the stored PDF
- `POST /rest/ros` — send praise
- `POST /rest/feil-og-mangler` — report bugs or missing functionality
- `POST /rest/bestilling-av-samtale` — request a Sami-language conversation
- health/readiness endpoints such as `/isAlive`, `/isReady`, and `/health/status`

## Main business flows

### Service complaints

This is the most feature-rich flow. The application:

1. validates the incoming request
2. generates a **PDF**
3. stores metadata in **Postgres**
4. archives the complaint in **Joark**
5. creates a task in **Oppgave / Gosys**
6. forwards analytics data to **BigQuery**

### Caseworker classification

For classification, the application:

1. fetches complaint data and documents from **SAF**
2. checks the task state in **Oppgave**
3. stores classification data in the local database
4. forwards classification data to **BigQuery**

### Praise, bug reports, and conversation requests

These flows are simpler. They are mainly:

1. validated
2. forwarded as email through **Microsoft Graph / Azure AD**

## Important internal areas in `app/`

| Path | Responsibility |
| --- | --- |
| `rest/` | Controllers grouped by domain: `serviceklage`, `ros`, `feilogmangler`, `bestillingavsamtale` |
| `consumer/` | Integrations with external systems such as `joark`, `oppgave`, `saf`, `pdl`, `ereg`, `norg2`, and `email` |
| `repository/` | Spring Data repositories |
| `domain/models/` | JPA entities |
| `bigquery/` | Export and cleanup of analytics data |
| `config/` | Security, OAuth2 clients, HTTP clients, GraphQL, caching, Jackson, and scheduling |

## Authentication and authorization

The service accepts JWTs from two issuers:

- `azuread`
- `tokenx`

Most user-facing endpoints allow either issuer.

Caseworker classification endpoints require **Azure AD** with the `serviceklage-klassifisering` access claim.

## Data and infrastructure

- Local schema changes are managed with **Flyway**
- Migrations live under `app/src/main/resources/db/migration/`
- Local development uses `docker-compose.yml`

The local setup starts:

- a mock OAuth server
- Postgres
- WireMock stubs for Joark, Oppgave, NORG2, SAF, PDL, and EREG

## `api/` module

The `api/` module contains the OpenAPI definition:

- `api/src/main/resources/tilbakemeldingsmottak-api.yml`

Maven uses this spec to generate controller interfaces and API models. The controllers in `app/` implement those generated interfaces rather than manually maintained contracts.

## `pdfutility/` module

The `pdfutility/` module is a focused library for generating **PDF/A** complaint documents from HTML templates using:

- **Handlebars**
- **OpenHTMLtoPDF**
- **PDFBox**

## CI/CD

- Pull requests run `mvn clean install`
- Pushes to `main` build the application, build and push a Docker image, and deploy through **NAIS**

## Summary

In short, this repository is a **multi-module backend service for collecting, routing, storing, archiving, and classifying NAV user feedback**, built around OpenAPI-generated contracts, Spring Boot business logic, Postgres persistence, PDF generation, and several NAV/internal integrations.
