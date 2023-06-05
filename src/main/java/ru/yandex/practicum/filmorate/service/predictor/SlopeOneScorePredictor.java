package ru.yandex.practicum.filmorate.service.predictor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("slopeOneScorePredictor")
public class SlopeOneScorePredictor implements Predictor {

    private final FilmStorage filmStorage;
    private final Map<Integer, HashMap<Integer, Double>> inputData = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Double>> diff = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Integer>> freq = new HashMap<>();
    private final List<Integer> allFilmsIds = new ArrayList<>();
    private static final int MIN_RECOMMENDATION_SCORE = 5;
    private static final int MAX_SIMILAR_USERS_COUNT = 10;

    @Autowired
    public SlopeOneScorePredictor(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> recommendFilms(int userId) {
        filmStorage.getFilmScoresStats(inputData, allFilmsIds);
        chooseSimilarUsers(userId, inputData);
        getDifferencesMatrix(inputData);
        predict(inputData);
        List<Integer> recommendationsId = new ArrayList<>();

        if (outputData.containsKey(userId)) {
            for (int filmId : outputData.get(userId).keySet()) {
                if (!inputData.get(userId).containsKey(filmId)
                        && outputData.get(userId).get(filmId) > MIN_RECOMMENDATION_SCORE) {
                    recommendationsId.add(filmId);
                }
            }
        }

        if (recommendationsId.isEmpty()) {
            return new ArrayList<>();
        }

        return filmStorage.getFilmsWhereIdEquals(recommendationsId);
    }

    /**
     * Check similarity between users based on their ratings and get
     * 10 most similar users to make recommendations
     *
     * @param userId
     * @param inputData
     */
    private void chooseSimilarUsers(Integer userId, Map<Integer, HashMap<Integer, Double>> inputData) {
        HashMap<Integer, Double> userRatings = inputData.get(userId);
        HashMap<Integer, Double> userSimilarityCoefficients = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Double>> e : inputData.entrySet()) {
            if (!Objects.equals(e.getKey(), userId)) {
                List<Double> userVector1 = new ArrayList<>();
                List<Double> userVector2 = new ArrayList<>();
                for (Integer filmId : userRatings.keySet()) {
                    if (e.getValue().containsKey(filmId)) {
                        userVector1.add(userRatings.get(filmId));
                        userVector2.add(e.getValue().get(filmId));
                    }
                }
                Double coeff = getCosineSimilarity(userVector1, userVector2);
                userSimilarityCoefficients.put(e.getKey(), coeff);
            }
        }

        Map<Integer, Double> sortedMap =
                userSimilarityCoefficients.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(MAX_SIMILAR_USERS_COUNT)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        for (Integer id : inputData.keySet()) {
            if (!sortedMap.containsKey(id) && !Objects.equals(id, userId)) {
                inputData.remove(id);
            }
        }
    }

    /**
     * Calculates the cosine similarity coefficient between two users
     *
     * @param v1
     * @param v2
     */
    private double getCosineSimilarity(List<Double> v1, List<Double> v2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Calculates the supporting differences and frequencies matrices
     * to make predictions about film recommendations for user
     *
     * @param inputData
     */
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

    /**
     * Makes predictions based on selected similar users and their
     * diff and freq matrices
     *
     * @param inputData
     */
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