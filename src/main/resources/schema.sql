
DROP TABLE IF EXISTS APP_USERS CASCADE;
CREATE TABLE APP_USERS (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	USER_NAME CHARACTER VARYING(45),
	LOGIN CHARACTER VARYING(45) NOT NULL,
	EMAIL CHARACTER VARYING(45) NOT NULL,
	BIRTHDAY DATE NOT NULL
);

DROP TABLE IF EXISTS RATINGS CASCADE;
CREATE TABLE RATINGS (
	RATING_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	RATING_NAME CHARACTER VARYING(25) NOT NULL
);

DROP TABLE IF EXISTS FILMS CASCADE;
CREATE TABLE FILMS (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	FILM_NAME CHARACTER VARYING(45) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER NOT NULL,
	RATING_ID INTEGER NOT NULL REFERENCES RATINGS(RATING_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS GENRES CASCADE;
CREATE TABLE GENRES (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	GENRE_NAME CHARACTER VARYING(25) NOT NULL
);

DROP TABLE IF EXISTS FILMS_GENRES CASCADE;
CREATE TABLE FILMS_GENRES (
	FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
	GENRE_ID INTEGER NOT NULL REFERENCES GENRES(GENRE_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS LIKES CASCADE;
CREATE TABLE LIKES (
	FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
	USER_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS FRIENDS CASCADE;
CREATE TABLE FRIENDS (
	USER_ONE_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE,
	USER_TWO_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE
);