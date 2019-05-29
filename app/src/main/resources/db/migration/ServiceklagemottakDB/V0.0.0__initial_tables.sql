CREATE TABLE serviceklage (
  id                      NUMBER(3) NOT NULL,
  email                   VARCHAR(320) NOT NULL,
  klagetekst              VARCHAR(512) NOT NULL,
  tilbakemelding          VARCHAR(512) NOT NULL
);

ALTER TABLE serviceklage ADD CONSTRAINT serviceklage_pk PRIMARY KEY (id);

CREATE SEQUENCE serviceklage_seq;