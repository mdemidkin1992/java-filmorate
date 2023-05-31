package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.UserMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage extends DBStorage implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name empty. Set user name {}", user.getLogin());
            user.setName(user.getLogin());
        }
        addUserToDb(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        jdbcTemplate.update(SqlQueries.UPDATE_USER,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUserById(int userId) {
        User user = jdbcTemplate.query(SqlQueries.GET_USER, new UserMapper(), userId).stream().findAny().orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(SqlQueries.GET_USERS, new UserMapper());
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = jdbcTemplate.query(SqlQueries.GET_USER, new UserMapper(), userId).stream().findAny().orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
        return jdbcTemplate.query(SqlQueries.GET_FRIENDS, new UserMapper(), userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.update(SqlQueries.ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);
        jdbcTemplate.update(SqlQueries.DELETE_FRIEND, userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        Set<User> userFriends = new HashSet<>(jdbcTemplate.query(SqlQueries.GET_FRIENDS, new UserMapper(), userId));
        Set<User> otherFriends = new HashSet<>(jdbcTemplate.query(SqlQueries.GET_FRIENDS, new UserMapper(), otherId));

        Set<User> intersection = new HashSet<>(userFriends);
        intersection.retainAll(otherFriends);

        return new ArrayList<>(intersection);
    }

    @Override
    public void deleteUserById(int userId) {
        User user = jdbcTemplate.query(SqlQueries.GET_USER,new UserMapper(),userId).stream().findAny().orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
        jdbcTemplate.update(SqlQueries.DELETE_USER_BY_ID,userId);
    }

    private void addUserToDb(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SqlQueries.ADD_USER, new String[]{"USER_ID"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
    }

    public void clearDb() {
        jdbcTemplate.update("DELETE FROM APP_USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM SCORES");
        jdbcTemplate.update("DELETE FROM FRIENDS");
    }
}