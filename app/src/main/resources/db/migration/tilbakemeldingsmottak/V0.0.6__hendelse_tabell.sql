CREATE SEQUENCE hendelse_id_seq;

CREATE TABLE hendelse
(
	id            BIGINT                   NOT NULL PRIMARY KEY DEFAULT nextval('hendelse_id_seq'::regclass),
	journalpostid VARCHAR(255),
	oppgaveid     VARCHAR(255),
	hendelsetype  VARCHAR(255)             NOT NULL,
	tidspunkt     TIMESTAMP WITH TIME ZONE NOT NULL             default (now() at time zone 'UTC')
);

CREATE INDEX hendelse_journalpostid_idx ON hendelse (journalpostid);
CREATE INDEX hendelse_oppgaveid_idx ON hendelse (oppgaveid);
CREATE INDEX hendelse_hendelsetype_idx ON hendelse (hendelsetype);
