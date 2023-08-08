-- Inserting data into the specialization dictionary
INSERT INTO dictionary (name) VALUES ('specializations');
INSERT INTO dictionaryvalue (dictionary_id, name) VALUES (1, 'kardiolog');
INSERT INTO dictionaryvalue (dictionary_id, name) VALUES (1, 'chirurg');
INSERT INTO dictionaryvalue (dictionary_id, name) VALUES (1, 'ortopeda');

-- Inserting doctors data
INSERT INTO doctors (id_doctor, firstName, lastName, birthDate, pesel, nip, specialization_id)
VALUES (1, 'Adam', 'Nowak', '1980-01-01', '80010112345', '123-456-11-11', 1);

INSERT INTO doctors (id_doctor, firstName, lastName, birthDate, pesel, nip, specialization_id)
VALUES (2, 'Anna', 'Kowal', '1990-02-02', '90020212345', '123-456-22-22', 2);

-- Inserting patients data
INSERT INTO patients (id_patient, firstName, lastName, birthDate, pesel)
VALUES (1, 'Karol', 'Madej', '1985-10-10', '85101012345');

INSERT INTO patients (id_patient, firstName, lastName, birthDate, pesel)
VALUES (2, 'Kalina', 'Wnuk', '1995-12-12', '95121212345');

-- Inserting home visits data
INSERT INTO visits (id_visit, doctor_id, patient_id, visitDate)
VALUES (1, 1, 1, '2023-08-01');

INSERT INTO visits (id_visit, doctor_id, patient_id, visitDate)
VALUES (2, 2, 1, '2023-08-02');

INSERT INTO visits (id_visit, doctor_id, patient_id, visitDate)
VALUES (3, 2, 2, '2023-08-03');