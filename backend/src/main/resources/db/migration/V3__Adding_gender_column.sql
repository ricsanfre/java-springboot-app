ALTER TABLE customer
ADD COLUMN gender TEXT not null;

ALTER TABLE customer
ADD  CONSTRAINT gender_values check (gender in ('MALE', 'FEMALE'));
