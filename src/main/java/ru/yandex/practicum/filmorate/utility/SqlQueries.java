package ru.yandex.practicum.filmorate.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SqlQueries {

    // APP_USERS
    public static final String GET_USERS = "SELECT * FROM APP_USERS";
    public static final String GET_USER = "SELECT * FROM APP_USERS WHERE USER_ID = ?";
    public static final String ADD_USER = "INSERT INTO APP_USERS (USER_NAME, LOGIN, EMAIL, BIRTHDAY) VALUES(?, ?, ?, ?)";
    public static final String UPDATE_USER = "UPDATE APP_USERS SET USER_NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?";

    public static final String GET_USERS_LIKES =
            "SELECT * FROM APP_USERS au " +
                    "JOIN LIKES l ON au.USER_ID = l.USER_ID " +
                    "JOIN FILMS f ON f.FILM_ID = l.FILM_ID " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE au.USER_ID = ?";

    public static final String DELETE_USER_BY_ID = "DELETE FROM APP_USERS WHERE USER_ID = ?";

    // FRIENDS
    public static final String GET_FRIENDS = "SELECT * FROM APP_USERS au JOIN FRIENDS f ON au.USER_ID = f.USER_TWO_ID WHERE f.USER_ONE_ID = ?";
    public static final String ADD_FRIEND = "INSERT INTO FRIENDS (USER_ONE_ID, USER_TWO_ID) VALUES (?, ?)";
    public static final String DELETE_FRIEND = "DELETE FROM FRIENDS WHERE USER_ONE_ID = ? AND USER_TWO_ID = ?";

    // FILMS
    public static final String GET_FILMS =
            "SELECT * FROM FILMS f " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = fd.DIRECTOR_ID";

    public static final String GET_FILM =
            "SELECT * FROM FILMS f " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE f.FILM_ID = ?";

    public static final String ADD_FILM = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_FILM = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
    public static final String ADD_FILMS_GENRES = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    public static final String ADD_FILMS_DIRECTORS = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
    public static final String DELETE_FILMS_GENRES = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    public static final String DELETE_FILMS_DIRECTORS = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = ?";

    public static final String FIND_FILMS_BY_NAME =
            "SELECT * " +
                    "FROM FILMS f " +
                    "LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "LEFT JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE LOWER(f.FILM_NAME) LIKE ? " +
                    "ORDER BY temp.LIKES_COUNT DESC";

    public static final String FIND_FILMS_BY_DIRECTOR =
            "SELECT * " +
                    "FROM FILMS f " +
                    "LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "LEFT JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd on f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE LOWER(d.DIRECTOR_NAME) LIKE ? " +
                    "ORDER BY temp.LIKES_COUNT DESC";

    public static final String FIND_FILMS_BY_NAME_OR_DIRECTOR =
            "SELECT * " +
                    "FROM FILMS f " +
                    "LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "LEFT JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd on f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE LOWER(d.DIRECTOR_NAME) LIKE ? OR LOWER(f.FILM_NAME) LIKE ? " +
                    "ORDER BY temp.LIKES_COUNT DESC";

    public static final String FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_LIKES =
            "SELECT * " +
                    "FROM FILMS f " +
                    "LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "LEFT JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd on f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE d.DIRECTOR_ID = ? " +
                    "ORDER BY temp.LIKES_COUNT DESC";

    public static final String FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_YEAR =
            "SELECT * " +
                    "FROM FILMS f LEFT JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "LEFT JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd on f.FILM_ID = fd.FILM_ID " +
                    "LEFT JOIN DIRECTORS d on fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE d.DIRECTOR_ID = ? " +
                    "ORDER BY EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS DATE))";

    public static final String DELETE_FILMS_BY_ID = "DELETE FROM FILMS WHERE FILM_ID = ?";

    // LIKES
    public static final String ADD_LIKE = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    public static final String DELETE_LIKE = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    public static final String GET_POPULAR_FILMS =
            "SELECT * FROM FILMS f LEFT " +
                    "JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "ORDER BY temp.LIKES_COUNT DESC";
    public static final String GET_POPULAR_FILMS_BY_GENRE_ID_AND_YEAR =
            "SELECT * FROM FILMS f LEFT " +
                    "JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE f.FILM_ID IN (SELECT fg.FILM_ID FROM FILMS_GENRES AS fg WHERE fg.GENRE_ID = ?) AND " +
                    "EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS DATE)) = ? " +
                    "GROUP BY f.FILM_ID, FG.GENRE_ID " +
                    "ORDER BY temp.LIKES_COUNT DESC ";
    public static final String GET_POPULAR_FILMS_BY_GENRE_ID =
            "SELECT * FROM FILMS f LEFT " +
                    "JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE f.FILM_ID IN (SELECT fg.FILM_ID FROM FILMS_GENRES fg WHERE fg.GENRE_ID = ? )" +
                    "ORDER BY temp.LIKES_COUNT DESC";
    public static final String GET_POPULAR_FILMS_BY_YEAR =
            "SELECT * FROM FILMS f LEFT " +
                    "JOIN (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT FROM LIKES l GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID " +
                    "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                    "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                    "LEFT JOIN GENRES g ON g.GENRE_ID = fg.GENRE_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS DATE)) = ? " +
                    "GROUP BY f.FILM_ID, FG.GENRE_ID " +
                    "ORDER BY temp.LIKES_COUNT DESC";

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
            "SELECT EVENT_ID, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, EVENT_TIMESTAMP FROM EVENTS WHERE USER_ID = ? "
                    + "ORDER BY EVENT_TIMESTAMP";

    public static final String GET_USER_EVENT_BY_ENTITY_ID_AND_EVENT_TYPE =
            "SELECT EVENT_ID, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID, EVENT_TIMESTAMP FROM EVENTS "
                    + "WHERE ENTITY_ID = ? AND EVENT_TYPE = ? AND OPERATION = ?";

    // DIRECTORS
    public static final String GET_DIRECTORS = "SELECT * FROM DIRECTORS";
    public static final String GET_DIRECTOR = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    public static final String UPDATE_DIRECTOR = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
    public static final String DELETE_DIRECTOR = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    public static final String CLEAR_TABLE_AND_RESET_ID =
            "DELETE FROM DIRECTORS; " +
                    "ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1";

    // REVIEWS
    public static final String GET_REVIEWS = "SELECT * FROM REVIEWS_VIEW ORDER BY USEFUL DESC LIMIT ?";
    public static final String GET_REVIEWS_FOR_FILM = "SELECT * FROM REVIEWS_VIEW WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
    public static final String ADD_REVIEW = "INSERT INTO REVIEWS (FILM_ID, USER_ID, CONTENT, IS_POSITIVE) VALUES(?, ?, ?, ?)";
    public static final String GET_REVIEW = "SELECT * FROM REVIEWS_VIEW WHERE REVIEW_ID = ?";
    public static final String UPDATE_REVIEW = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
    public static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
    public static final String ADD_REVIEW_LIKE = "INSERT INTO REVIEWS_RATING (REVIEW_ID, USER_ID, IS_REVIEW_POSITIVE) VALUES (?, ?, true)";
    public static final String ADD_REVIEW_DISLIKE = "INSERT INTO REVIEWS_RATING (REVIEW_ID, USER_ID, IS_REVIEW_POSITIVE) VALUES (?, ?, false)";
    public static final String DELETE_REVIEW_LIKE = "DELETE FROM REVIEWS_RATING WHERE REVIEW_ID = ?, USER_ID = ? IS_REVIEW_POSITIVE = true";
    public static final String DELETE_REVIEW_DISLIKE = "DELETE FROM REVIEWS_RATING WHERE REVIEW_ID = ?, USER_ID = ? IS_REVIEW_POSITIVE = false";

}