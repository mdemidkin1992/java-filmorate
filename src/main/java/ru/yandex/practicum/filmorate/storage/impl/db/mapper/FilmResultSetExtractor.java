package ru.yandex.practicum.filmorate.storage.impl.db.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            Film film;
            if (filmMap.get(rs.getInt("FILM_ID")) != null) {
                film = filmMap.get(rs.getInt("FILM_ID"));
            } else {
                film = extractFilmData(rs);
            }
            film.setGenres(extractGenresData(rs, film));
            film.setMpa(extractRatingData(rs));
            film.setDirectors(extractDirectorsData(rs, film));
            filmMap.put(film.getId(), film);
        }
        return new ArrayList<>(filmMap.values());
    }

    private Film extractFilmData(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(Date.valueOf(rs.getString("RELEASE_DATE")).toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
    }

    private List<Genre> extractGenresData(ResultSet rs, Film film) throws SQLException {
        Genre genre = Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();

        if (genre.getId() != 0)
            film.getGenres().add(genre);

        return film.getGenres();
    }

    private Rating extractRatingData(ResultSet rs) throws SQLException {
        return Rating.builder()
                .id(rs.getInt("RATING_ID"))
                .name(rs.getString("RATING_NAME"))
                .build();
    }

    private Set<Director> extractDirectorsData(ResultSet rs, Film film) throws SQLException {
        Director director = Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();

        if (director.getId() != 0)
            film.getDirectors().add(director);

        return film.getDirectors();
    }
}