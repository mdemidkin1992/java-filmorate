package ru.yandex.practicum.filmorate.storage.impl.db;

public class SqlQueries {
    // APP_USERS
    static final String GET_USERS = "SELECT * FROM APP_USERS";
    static final String GET_USER = "SELECT * FROM APP_USERS WHERE USER_ID = ?";
    static final String ADD_USER = "INSERT INTO APP_USERS (USER_NAME, LOGIN, EMAIL, BIRTHDAY) VALUES(?, ?, ?, ?)";
    static final String UPDATE_USER = "UPDATE APP_USERS SET USER_NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";

    // FRIENDS
    static final String GET_FRIENDS = "SELECT * FROM APP_USERS au JOIN FRIENDS f ON au.USER_ID = f.USER_TWO_ID WHERE f.USER_ONE_ID = ?";
    static final String ADD_FRIEND = "INSERT INTO FRIENDS (USER_ONE_ID, USER_TWO_ID) VALUES (?, ?)";
    static final String DELETE_FRIEND = "DELETE FROM FRIENDS WHERE USER_ONE_ID = ? AND USER_TWO_ID = ?";

    // FILMS
    static final String GET_FILMS = "SELECT * FROM FILMS";
    static final String GET_FILM = "SELECT * FROM FILMS f WHERE FILM_ID = ?";
    static final String GET_FILM_RATING = "SELECT r.RATING_ID, r.RATING_NAME FROM RATINGS r JOIN FILMS f ON r.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";
    static final String GET_FILM_GENRES = "SELECT g.GENRE_ID, g.GENRE_NAME FROM GENRES g JOIN FILMS_GENRES fg ON g.GENRE_ID = fg.GENRE_ID JOIN FILMS f ON fg.FILM_ID = f.FILM_ID WHERE f.FILM_ID = ?";
    static final String ADD_FILM = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
    static final String UPDATE_FILM = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
    static final String ADD_FILMS_GENRES = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    static final String DELETE_FILMS_GENRES = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";

    // LIKES
    static final String ADD_LIKE = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    static final String DELETE_LIKE = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    static final String GET_POPULAR_FILMS = "SELECT * FROM FILMS f LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS likes_count FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID ORDER BY temp.LIKES_COUNT DESC;";

    // RATINGS
    static final String GET_RATINGS = "SELECT * FROM RATINGS";
    static final String GET_RATING = "SELECT * FROM RATINGS WHERE RATING_ID = ?";

    // GENRES
    static final String GET_GENRES = "SELECT * FROM GENRES";
    static final String GET_GENRE = "SELECT * FROM GENRES WHERE GENRE_ID = ?";

}
