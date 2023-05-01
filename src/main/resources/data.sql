--INSERT INTO APP_USERS (USER_NAME, LOGIN, EMAIL, BIRTHDAY)
--VALUES  ('Mark', 	'marklogin', 	'mark@email.com', 	'1992-01-02'),
--        ('Ben', 	'benlogin', 	'ben@email.com', 	'1995-02-04'),
--        ('Clark',	'clarklogin', 	'clark@email.com', 	'1997-04-06'),
--        ('Joe', 	'joelogin', 	'joe@email.com', 	'2000-06-10'),
--        ('John', 	'johnlogin', 	'john@email.com', 	'2001-08-12');

INSERT INTO RATINGS (RATING_NAME)
VALUES  ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

--INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
--VALUES  ('Titanic', 	'Nothing on Earth can separate them', 		'1997-11-01', 	194,    3),
--        ('Avatar', 		'This is the new world', 					'2009-12-17', 	162,    3),
--        ('Fight Club', 	'Intrigue. Chaos. Soap', 					'1999-09-11', 	139,    4),
--        ('Paddington', 	'Please look after this bear. Thank you', 	'2014-11-23', 	95,     2),
--        ('The Holiday', 'Cheerful love comedy', 					'2006-12-07', 	136,    3);

INSERT INTO GENRES (GENRE_NAME)
VALUES  ('Комедия'), ('Драма'), ('Мультфильм'),
        ('Триллер'), ('Документальный'), ('Боевик');

--INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID)
--VALUES 	(1, 2), (2, 2), (2, 6), (3, 4), (3, 6),
--        (4, 1), (4, 3), (1, 5), (5, 1), (5, 2);

--INSERT INTO LIKES (FILM_ID, USER_ID)
--VALUES  (1, 1), (1, 2), (1, 3), (2, 1), (2, 3),
--        (2, 4), (2, 5), (3, 5), (4, 1), (4, 3);

--INSERT INTO FRIENDS (USER_ONE_ID, USER_TWO_ID, STATUS)
--VALUES  (1, 2, 'PENDING'), (1, 3, 'APPROVE'), (2, 3, 'PENDING'),
--        (2, 4, 'APPROVE'), (2, 5, 'PENDING'), (3, 4, 'PENDING'),
--        (4, 1, 'APPROVE'), (4, 5, 'PENDING'), (5, 1, 'APPROVE');