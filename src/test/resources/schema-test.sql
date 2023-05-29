DROP TABLE IF EXISTS APP_USERS CASCADE;
CREATE TABLE APP_USERS
(
    USER_ID   INTEGER               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    USER_NAME CHARACTER VARYING(45),
    LOGIN     CHARACTER VARYING(45) NOT NULL,
    EMAIL     CHARACTER VARYING(45) NOT NULL,
    BIRTHDAY  DATE                  NOT NULL
);

DROP TABLE IF EXISTS RATINGS CASCADE;
CREATE TABLE RATINGS
(
    RATING_ID   INTEGER               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    RATING_NAME CHARACTER VARYING(25) NOT NULL
);

DROP TABLE IF EXISTS FILMS CASCADE;
CREATE TABLE FILMS
(
    FILM_ID      INTEGER                NOT NULL AUTO_INCREMENT PRIMARY KEY,
    FILM_NAME    CHARACTER VARYING(45)  NOT NULL,
    DESCRIPTION  CHARACTER VARYING(200) NOT NULL,
    RELEASE_DATE DATE                   NOT NULL,
    DURATION     INTEGER                NOT NULL,
    RATING_ID    INTEGER                NOT NULL REFERENCES RATINGS (RATING_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS GENRES CASCADE;
CREATE TABLE GENRES
(
    GENRE_ID   INTEGER               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    GENRE_NAME CHARACTER VARYING(25) NOT NULL
);

DROP TABLE IF EXISTS FILMS_GENRES CASCADE;
CREATE TABLE FILMS_GENRES
(
    FILM_ID  INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    GENRE_ID INTEGER NOT NULL REFERENCES GENRES (GENRE_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS LIKES CASCADE;
CREATE TABLE LIKES
(
    FILM_ID INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID INTEGER NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS SCORES CASCADE;
CREATE TABLE SCORES
(
    FILM_ID INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID INTEGER NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE,
    SCORE INTEGER NOT NULL
);

DROP TABLE IF EXISTS FRIENDS CASCADE;
CREATE TABLE FRIENDS
(
    USER_ONE_ID INTEGER NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE,
    USER_TWO_ID INTEGER NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS DIRECTORS CASCADE;
CREATE TABLE DIRECTORS
(
    DIRECTOR_ID   INTEGER               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    DIRECTOR_NAME CHARACTER VARYING(45) NOT NULL
);

DROP TABLE IF EXISTS FILMS_DIRECTORS CASCADE;
CREATE TABLE FILMS_DIRECTORS
(
    FILM_ID     INTEGER NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    DIRECTOR_ID INTEGER NOT NULL REFERENCES DIRECTORS (DIRECTOR_ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS REVIEWS CASCADE;
CREATE TABLE REVIEWS
(
    REVIEW_ID   INTEGER                 NOT NULL AUTO_INCREMENT PRIMARY KEY,
    FILM_ID     INTEGER                 NOT NULL REFERENCES FILMS (FILM_ID) ON DELETE CASCADE,
    USER_ID     INTEGER                 NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE,
    CONTENT     CHARACTER VARYING(4096) NOT NULL,
    IS_POSITIVE BOOLEAN                 NOT NULL
);

DROP TABLE IF EXISTS REVIEWS_RATING CASCADE;
CREATE TABLE REVIEWS_RATING
(
    REVIEW_ID          INTEGER NOT NULL REFERENCES REVIEWS (REVIEW_ID) ON DELETE CASCADE,
    USER_ID            INTEGER NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE,
    IS_REVIEW_POSITIVE BOOLEAN DEFAULT NULL
);

DROP VIEW IF EXISTS REVIEWS_VIEW CASCADE;
CREATE VIEW REVIEWS_VIEW AS
SELECT R.REVIEW_ID,
       R.CONTENT,
       R.IS_POSITIVE,
       R.USER_ID,
       R.FILM_ID,
       SUM(CASE WHEN IS_REVIEW_POSITIVE THEN 1 WHEN NOT IS_REVIEW_POSITIVE THEN -1 ELSE 0 END) AS USEFUL
FROM REVIEWS R
         LEFT JOIN REVIEWS_RATING AS RR ON R.REVIEW_ID = RR.REVIEW_ID
GROUP BY R.REVIEW_ID;


DROP TABLE IF EXISTS EVENTS CASCADE;
CREATE TABLE EVENTS
(
    EVENT_ID        INTEGER                     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    USER_ID         INTEGER                     NOT NULL REFERENCES APP_USERS (USER_ID) ON DELETE CASCADE,
    EVENT_TYPE      CHARACTER VARYING(45)       NOT NULL,
    OPERATION       CHARACTER VARYING(45)       NOT NULL,
    ENTITY_ID       INTEGER                     NOT NULL,
    EVENT_TIMESTAMP TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "EVENTS_EVENT_TYPE_check" CHECK (EVENT_TYPE::text IN ('LIKE', 'REVIEW', 'FRIEND')),
    CONSTRAINT "EVENTS_OPERATION_check" CHECK (OPERATION::text IN ('REMOVE', 'ADD', 'UPDATE'))
);