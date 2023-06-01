package util;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.number.IntegerRandomizer;
import org.jeasy.random.randomizers.number.LongRandomizer;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;


public class CustomEasyRandom {
    static List<Rating> RATING_LIST = List.of(
            new Rating(1, "G"),
            new Rating(2, "PG"),
            new Rating(3, "PG-13"),
            new Rating(4, "R"),
            new Rating(5, "NC-17")
    );
    public static List<Genre> GENRE_LIST = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );
    public static Long SEED = 123L;
    static LocalDate START_DATE = LocalDate.of(1990, 1, 1);
    static LocalDate END_DATE = LocalDate.of(2010, 12, 12);
    static int STRING_START_LENGTH = 5;
    static int STRING_END_LENGTH = 10;

    static EasyRandom easyRandom = new EasyRandom(
            new EasyRandomParameters()
                    .seed(SEED)
                    .dateRange(START_DATE, END_DATE)
                    .stringLengthRange(STRING_START_LENGTH, STRING_END_LENGTH)
                    .randomize(Integer.class, new smallPositiveIntRandomizer(SEED))
                    .randomize(Long.class, new smallPositiveLongRandomizer(SEED))
    );

    static class smallPositiveIntRandomizer extends IntegerRandomizer {
        public smallPositiveIntRandomizer(long seed) {
            super(seed);
        }

        @Override
        public Integer getRandomValue() {
            return random.nextInt(1024);
        }
    }

    static class smallPositiveLongRandomizer extends LongRandomizer {
        public smallPositiveLongRandomizer(long seed) {
            super(seed);
        }

        @Override
        public Long getRandomValue() {
            return (long) random.nextInt(1024);
        }

    }

    public static User nextUser() {
        return easyRandom.nextObject(User.class);
    }

    public static Film nextFilm() {
        Film film = easyRandom.nextObject(Film.class);
        film.setMpa(RATING_LIST.get(new Random().nextInt(RATING_LIST.size())));
        film.setGenres(List.of(
                        GENRE_LIST.get(easyRandom.nextInt(GENRE_LIST.size())),
                        GENRE_LIST.get(easyRandom.nextInt(GENRE_LIST.size())),
                        GENRE_LIST.get(easyRandom.nextInt(GENRE_LIST.size()))
                )
        );
        film.setDirectors(null);
        return film;
    }

    public static Film nextFilm(int year) {
        Film film = nextFilm();
        film.setReleaseDate(LocalDate.of(year, 1, 1));
        return film;
    }

    public static Genre getGenre(int id) {
        return GENRE_LIST.get(id - 1);
    }
}
