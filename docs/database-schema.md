# Database schema

This document describes the database schema defined by the Flyway migrations under:

- `app/src/main/resources/db/migration/tilbakemeldingsmottak/`

## Overview

The application schema currently consists of:

1. `serviceklage` — the main table for submitted and classified service complaints
2. `hendelse` — an event/history table tied to service complaint processing

There are **no foreign key constraints** in the Flyway migrations. Relations are maintained logically through shared identifiers such as `journalpostid`, `oppgaveid`, and `journalpost_id`.

## Table: `serviceklage`

This is the primary table for storing complaint data, classification data, and processing metadata.

### Columns

| Column | Type | Nullable | Description |
| --- | --- | --- | --- |
| `SERVICEKLAGE_ID` | `SERIAL` | No | Primary key |
| `JOURNALPOST_ID` | `VARCHAR(11)` | Yes | Archive/journal reference |
| `OPPRETTET_DATO` | `TIMESTAMP` | No | Time the complaint was created |
| `KLAGEN_GJELDER_ID` | `VARCHAR(15)` | Yes | Identifier for the person/entity the complaint concerns |
| `KLAGETYPER` | `VARCHAR(200)` | Yes | Complaint categories |
| `GJELDER_SOSIALHJELP` | `VARCHAR(100)` | Yes | Whether the complaint concerns social assistance |
| `KLAGETEKST` | `TEXT` | Yes | Complaint text |
| `BEHANDLES_SOM_SERVICEKLAGE` | `VARCHAR(100)` | Yes | Whether it should be treated as a service complaint |
| `BEHANDLES_SOM_SERVICEKLAGE_UTDYPNING` | `VARCHAR(2000)` | Yes | Elaboration on the service complaint assessment |
| `FREMMET_DATO` | `TIMESTAMP` | Yes | Date/time the complaint was raised |
| `INNSENDER` | `VARCHAR(100)` | Yes | Sender type or sender description |
| `KANAL` | `VARCHAR(100)` | Yes | Submission channel |
| `KANAL_UTDYPNING` | `VARCHAR(2000)` | Yes | Channel details |
| `ENHETSNUMMER_PAAKLAGET` | `VARCHAR(4)` | Yes | Complained-about unit number |
| `ENHETSNUMMER_BEHANDLENDE` | `VARCHAR(4)` | Yes | Processing unit number |
| `GJELDER` | `VARCHAR(100)` | Yes | What the complaint concerns |
| `BESKRIVELSE` | `TEXT` | Yes | Description/details |
| `YTELSE` | `VARCHAR(100)` | Yes | Benefit/service area |
| `TEMA` | `VARCHAR(100)` | Yes | Topic |
| `TEMA_UTDYPNING` | `VARCHAR(2000)` | Yes | Topic details |
| `VEILEDNING` | `VARCHAR(100)` | Yes | Guidance-related field |
| `UTFALL` | `VARCHAR(100)` | Yes | Classification outcome |
| `AARSAK` | `VARCHAR(2000)` | Yes | Cause/reason |
| `TILTAK` | `VARCHAR(2000)` | Yes | Mitigation or follow-up action |
| `SVARMETODE` | `VARCHAR(100)` | Yes | Reply method |
| `SVARMETODE_UTDYPNING` | `VARCHAR(2000)` | Yes | Reply method details |
| `AVSLUTTET_DATO` | `TIMESTAMP` | Yes | Time the complaint/classification was completed |
| `SKJEMA_VERSJON` | `INTEGER` | Yes | Form version |
| `KLASSIFISERING_JSON` | `TEXT` | Yes | Serialized classification payload |
| `RELATERT` | `VARCHAR(200)` | Yes | Added in migration `V0.0.1` |
| `KLAGETYPE_UTDYPNING` | `VARCHAR(1000)` | Yes | Added in migration `V0.0.2` |
| `INNLOGGET` | `BOOLEAN` | Yes | Added as `SMALLINT`, later converted to `BOOLEAN` |
| `OPPGAVEID` | `VARCHAR(255)` | Yes | Task identifier, added in migration `V0.0.7` |

### Constraints and keys

- Primary key: `SERVICEKLAGE_ID`
- No explicit indexes are added by the migrations besides the primary key index
- No foreign keys are defined

### Notes

- `KLAGETEKST`, `BESKRIVELSE`, and `KLASSIFISERING_JSON` use `TEXT`
- `INNLOGGET` was introduced as `SMALLINT` and migrated to `BOOLEAN` with value mapping:
  - `1` -> `TRUE`
  - `0` -> `FALSE`
  - all other/null-ish values remain `NULL`

## Table: `hendelse`

This table stores lifecycle events related to complaint processing.

### Columns

| Column | Type | Nullable | Description |
| --- | --- | --- | --- |
| `id` | `SERIAL` | No | Primary key |
| `journalpostid` | `VARCHAR(255)` | Yes | Journal post reference |
| `oppgaveid` | `VARCHAR(255)` | Yes | Task reference |
| `hendelsetype` | `VARCHAR(255)` | No | Event type |
| `tidspunkt` | `TIMESTAMP WITH TIME ZONE` | No | Event timestamp, defaulting to current UTC time |

### Constraints and indexes

- Primary key: `id`
- Index: `hendelse_journalpostid_idx` on `journalpostid`
- Index: `hendelse_oppgaveid_idx` on `oppgaveid`
- No foreign keys are defined

### Notes

The migrations seed historical events from existing `serviceklage` rows:

- one `OPPRETT_SERVICEKLAGE` event for every existing complaint, using `opprettet_dato`
- one `KLASSIFISER_SERVICEKLAGE` event for complaints where `avsluttet_dato` is set

## Sequence behavior

The migration `V0.0.5__database_privs.sql` does not define privileges despite its name. It contains:

- `ALTER SEQUENCE IF EXISTS serviceklage_serviceklage_id_seq RESTART WITH 40530;`

This means the `serviceklage` primary-key sequence is reset to start from `40530`, presumably to continue from an earlier dataset.

## Migration history

| Migration | Change |
| --- | --- |
| `V0.0.0__initial_tables.sql` | Creates `serviceklage` |
| `V0.0.1__nytt_spoersmaal_relatert.sql` | Adds `RELATERT` to `serviceklage` |
| `V0.0.2__klagetype_utdypning.sql` | Adds `KLAGETYPE_UTDYPNING` to `serviceklage` |
| `V0.0.3__ny_kolonne_innlogget.sql` | Adds `INNLOGGET` as `SMALLINT` |
| `V0.0.4__endre_kolonne_innlogget.sql` | Converts `INNLOGGET` to `BOOLEAN` |
| `V0.0.5__database_privs.sql` | Resets `serviceklage_serviceklage_id_seq` to `40530` |
| `V0.0.6__hendelse_tabell.sql` | Creates `hendelse` and its indexes |
| `V0.0.7__serviceklage_oppgaveid.sql` | Adds `OPPGAVEID` to `serviceklage` |
| `V0.0.8__eksisterende_hendelser.sql` | Backfills `hendelse` from existing `serviceklage` rows |

## Current schema summary

In practice, the schema is centered around:

- a wide **`serviceklage`** table that stores both incoming complaint data and later classification metadata
- a smaller **`hendelse`** table that records processing events over time

The schema is intentionally simple: few constraints, no foreign keys, and only targeted indexes on event lookup fields.
