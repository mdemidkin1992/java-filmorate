package ru.yandex.practicum.filmorate.service.predictor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("simpleLikePredictor")
public class SimpleLikePredictor implements Predictor {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public SimpleLikePredictor(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage
    ) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> recommendFilms(int userId) {
        int otherUserId = userStorage.getOtherUserIdWithCommonInterests(userId);
        List<Film> userLikedFilms = filmStorage.getFilmsLikedByUser(userId);
        List<Film> otherUserLikedFilms = filmStorage.getFilmsLikedByUser(otherUserId);

        Set<Film> union = new HashSet<>(userLikedFilms);
        union.addAll(otherUserLikedFilms);
        union.removeAll(userLikedFilms);

        return new ArrayList<>(union);
    }

}
