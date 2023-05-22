package ru.yandex.practicum.filmorate.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SqlQueries {

    // APP_USERS
    public static final String GET_USERS = "SELECT * FROM APP_USERS";
    public static final String GET_USER = "SELECT * FROM APP_USERS WHERE USER_ID = ?";
    public static final String ADD_USER = "INSERT INTO APP_USERS (USER_NAME, LOGIN, EMAIL, BIRTHDAY) VALUES(?, ?, ?, ?)";
    public static final String UPDATE_USER = "UPDATE APP_USERS SET USER_NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";

    // FRIENDS
    public static final String GET_FRIENDS = "SELECT * FROM APP_USERS au JOIN FRIENDS f ON au.USER_ID = f.USER_TWO_ID WHERE f.USER_ONE_ID = ?";
    public static final String ADD_FRIEND = "INSERT INTO FRIENDS (USER_ONE_ID, USER_TWO_ID) VALUES (?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM FRIENDS WHERE USER_ONE_ID = ? AND USER_TWO_ID = ?";

    // FILMS
    public static final String GET_FILMS = "SELECT * FROM FILMS";
    public static final String GET_FILM = "SELECT * FROM FILMS f WHERE FILM_ID = ?";
    public static final String GET_FILM_RATING = "SELECT r.RATING_ID, r.RATING_NAME FROM RATINGS r JOIN FILMS f ON r.RATING_ID = f.RATING_ID WHERE f.FILM_ID = ?";
    public static final String GET_FILM_GENRES = "SELECT g.GENRE_ID, g.GENRE_NAME FROM GENRES g JOIN FILMS_GENRES fg ON g.GENRE_ID = fg.GENRE_ID JOIN FILMS f ON fg.FILM_ID = f.FILM_ID WHERE f.FILM_ID = ?";
    public static final String ADD_FILM = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_FILM = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
    public static final String ADD_FILMS_GENRES = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    public static final String DELETE_FILMS_GENRES = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    public static final String GET_GENRES_FOR_ALL_FILMS = "SELECT * FROM FILMS_GENRES fg JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID";
    public static final String GET_RATINGS_FOR_ALL_FILMS = "SELECT * FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID";

    // LIKES
    public static final String ADD_LIKE = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    public static final String DELETE_LIKE = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    public static final String GET_POPULAR_FILMS = "SELECT * FROM FILMS f LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS likes_count FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID ORDER BY temp.LIKES_COUNT DESC;";

    // RATINGS
    public static final String GET_RATINGS = "SELECT * FROM RATINGS";
    public static final String GET_RATING = "SELECT * FROM RATINGS WHERE RATING_ID = ?";

    // GENRES
    public static final String GET_GENRES = "SELECT * FROM GENRES";
    public static final String GET_GENRE = "SELECT * FROM GENRES WHERE GENRE_ID = ?";

    // EVENTS
    public static final String ADD_EVENT =
            "INSERT INTO EVENTS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, EVENT_TIMESTAMP) VALUES (?, ?, ?, ?, ?)";

    public static final String GET_USER_EVENTS =
            "SELECT EVENT_ID, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, EVENT_TIMESTAMP FROM EVENTS WHERE USER_ID = ?"
                    + "ORDER BY EVENT_TIMESTAMP DESC";
}