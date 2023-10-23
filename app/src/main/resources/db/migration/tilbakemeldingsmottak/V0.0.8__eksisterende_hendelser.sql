-- Create "opprett" events for existing serviceklage entries
INSERT INTO hendelse (journalpostid, oppgaveid, hendelsetype, tidspunkt)
SELECT
    s.journalpost_id,
    s.oppgaveid,
    'OPPRETT_SERVICEKLAGE' AS hendelsetype,
    s.opprettet_dato AS tidspunkt
FROM serviceklage s;

-- Create "klassifiser" events for existing serviceklage entries with avsluttet_dato not null
INSERT INTO hendelse (journalpostid, oppgaveid, hendelsetype, tidspunkt)
SELECT
    s.journalpost_id,
    s.oppgaveid,
    'KLASSIFISER_SERVICEKLAGE' AS hendelsetype,
    s.avsluttet_dato AS tidspunkt
FROM serviceklage s
WHERE s.avsluttet_dato IS NOT NULL;
