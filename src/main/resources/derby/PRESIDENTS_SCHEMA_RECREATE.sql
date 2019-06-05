--   -------------------------------------------------- 
--   Generated by Enterprise Architect Version 11.1.1112
--   Created On : Monday, 02 February, 2015 
--   DBMS       : DB2 
--   -------------------------------------------------- 

ALTER TABLE PRESIDENT DROP CONSTRAINT FK_PRESIDENT_STATE;

DROP TABLE STATE
;
CREATE TABLE STATE ( 
	id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	name VARCHAR(255) NOT NULL,
	abbreviation VARCHAR(255) NOT NULL UNIQUE
)
;
ALTER TABLE STATE ADD CONSTRAINT PK_STATE 
	PRIMARY KEY (id)
;

ALTER TABLE PRESIDENT DROP CONSTRAINT FK_PRESIDENT_PARTY;

DROP TABLE PARTY
;
--  Create Tables 
CREATE TABLE PARTY ( 
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	name VARCHAR(255) NOT NULL,
	founded_year INTEGER NOT NULL
	CHECK (founded_year BETWEEN 1789 AND 9999),
	end_year INTEGER
	CHECK (end_year BETWEEN 1789 AND 9999)
)
;
--  Create Primary Key Constraints 
ALTER TABLE PARTY ADD CONSTRAINT PK_Party 
	PRIMARY KEY (id)
;

ALTER TABLE PARTY
	ADD CONSTRAINT UQ_PARTY_name UNIQUE (name)
;

DROP TABLE PRESIDENT
;
CREATE TABLE PRESIDENT ( 
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	state_id INTEGER NOT NULL,
	party_id INTEGER,
	inaugurated_year INTEGER NOT NULL
	CHECK (inaugurated_year BETWEEN 1789 AND 9999),
	years DECIMAL(3,1) NOT NULL,
	CHECK (years BETWEEN 1 AND 12)
)
;
--COMMENT ON COLUMN PRESIDENT.years
--    IS 'Years in office'
--;

CREATE INDEX IXFK_PRESIDENT_PARTY ON PRESIDENT
	(party_id)
;
CREATE INDEX IXFK_PRESIDENT_STATE ON PRESIDENT
	(state_id)
;
ALTER TABLE PRESIDENT ADD CONSTRAINT PK_President 
	PRIMARY KEY (id, state_id)
;


ALTER TABLE PRESIDENT ADD CONSTRAINT FK_PRESIDENT_PARTY 
	FOREIGN KEY (party_id) REFERENCES PARTY (id)
;

ALTER TABLE PRESIDENT ADD CONSTRAINT FK_PRESIDENT_STATE 
	FOREIGN KEY (state_id) REFERENCES STATE (id)
;

--  Drop/Create View 
DROP VIEW PRESIDENTS_VIEW CASCADE
;
CREATE VIEW PRESIDENTS_VIEW AS
SELECT a.id, a.lastname, a.firstname, a.inaugurated_year, a. years, b.name AS "STATE", c.name AS "PARTY"
FROM PRESIDENT a
JOIN STATE b
ON a.state_id = b.id
LEFT OUTER JOIN PARTY c
ON a.party_id = c.id
ORDER BY 1
;