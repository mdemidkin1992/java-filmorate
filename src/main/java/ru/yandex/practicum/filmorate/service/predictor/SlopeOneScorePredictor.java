package ru.yandex.practicum.filmorate.service.predictor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("slopeOneScorePredictor")
public class SlopeOneScorePredictor implements Predictor {

    private final FilmStorage filmStorage;
    private final Map<Integer, HashMap<Integer, Double>> inputData = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Double>> diff = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Integer>> freq = new HashMap<>();
    private final List<Integer> allFilmsIds = new ArrayList<>();
    private static final int MIN_RECOMMENDATION_SCORE = 5;

    @Autowired
    public SlopeOneScorePredictor(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> recommendFilms(int userId) {
        filmStorage.getFilmScoresStats(inputData, allFilmsIds);
        getDifferencesMatrix(inputData);
        predict(inputData);
        List<Integer> recommendationsId = new ArrayList<>();

        for (int filmId : outputData.get(userId).keySet()) {
            if (!inputData.get(userId).containsKey(filmId)
                    && outputData.get(userId).get(filmId) > MIN_RECOMMENDATION_SCORE) {
                recommendationsId.add(filmId);
            }
        }

        if (recommendationsId.isEmpty()) {
            return new ArrayList<>();
        }

        return filmStorage.getFilmsWhereIdEquals(recommendationsId);
    }

    private void getDifferencesMatrix(Map<Integer, HashMap<Integer, Double>> inputData) {

        for (HashMap<Integer, Double> user : inputData.values()) {
            for (Map.Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                for (Map.Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    private void predict(Map<Integer, HashMap<Integer, Double>> inputData) {
        HashMap<Integer, Double> uPred = new HashMap<>();
        HashMap<Integer, Integer> uFreq = new HashMap<>();
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Map.Entry<Integer, HashMap<Integer, Double>> e : inputData.entrySet()) {
            for (Integer j : e.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j) + e.getValue().get(j);
                        double finalValue = predictedValue * freq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
            HashMap<Integer, Double> clean = new HashMap<>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }
            for (Integer j : allFilmsIds) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }

}