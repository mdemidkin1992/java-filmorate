//package ru.yandex.practicum.filmorate.controller;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.impl.mem.InMemoryUserStorage;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UserControllerTest {
//    private static UserController userController;
//    private static User user1, user2, user3, user4, user5;
//    private static final Validator VALIDATOR;
//
//    static {
//        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
//        VALIDATOR = validatorFactory.usingContext().getValidator();
//    }
//
//    @BeforeAll
//    public static void beforeAll() {
//        userController = new UserController(new UserService(new InMemoryUserStorage()));
//
//        user1 = User.builder().email("example_1@email.ru").login("login_1").name("name_1").birthday(Date.of(1990, 1, 10)).build();
//        user2 = User.builder().email("example_2@email.ru").login("login_2").name("name_2").birthday(Date.of(1991, 2, 11)).build();
//        user3 = User.builder().email("example_3@email.ru").login("login_3").name("name_3").birthday(Date.of(1992, 3, 12)).build();
//        user4 = User.builder().email("example_4@email.ru").login("login_4").name("name_4").birthday(LocalDate.of(1993, 4, 13)).build();
//        user5 = User.builder().email("example_5@email.ru").login("login_5").name("name_5").birthday(LocalDate.of(1994, 5, 14)).build();
//
//        userController.createUser(user1);
//        userController.createUser(user2);
//        userController.createUser(user3);
//        userController.createUser(user4);
//        userController.createUser(user5);
//    }
//
//    @Test
//    public void testValidations() {
//        User invalidUser = user1;
//        invalidUser.setEmail("wrong email.com@");
//        invalidUser.setLogin(null);
//        invalidUser.setName("wrong user name");
//        invalidUser.setBirthday(LocalDate.of(2030, 1, 1));
//
//        Set<ConstraintViolation<User>> validates = VALIDATOR.validate(invalidUser);
//        assertTrue(validates.size() > 0);
//        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
//    }
//
//    @Test
//    public void shouldGetAllUsers() {
//        List<User> expected = new ArrayList<>();
//        expected.add(user1);
//        expected.add(user2);
//        expected.add(user3);
//        expected.add(user4);
//        expected.add(user5);
//
//        List<User> actual = userController.getUsers();
//        assertEquals(expected, actual, "Not all users were added to storage.");
//    }
//
//    @Test
//    public void shouldAddFriendWhenIdIsCorrect() {
//        int userId = user1.getId();
//        int friendId1 = user2.getId();
//        int friendId2 = user3.getId();
//        int friendId3 = user4.getId();
//        int friendId4 = user5.getId();
//
//        userController.addFriend(userId, friendId1);
//        userController.addFriend(userId, friendId2);
//        userController.addFriend(userId, friendId3);
//        userController.addFriend(userId, friendId4);
//
//        Set<Long> expectedSet = new HashSet<>();
//        expectedSet.add((long) friendId1);
//        expectedSet.add((long) friendId2);
//        expectedSet.add((long) friendId3);
//        expectedSet.add((long) friendId4);
//
//        Set<Long> actualSet = userController.getUserById(userId).getFriends();
//
//        List<User> expectedList = new ArrayList<>();
//        expectedList.add(user2);
//        expectedList.add(user3);
//        expectedList.add(user4);
//        expectedList.add(user5);
//
//        List<User> actualList = userController.getUserFriends(userId);
//
//        List<User> expectedCommonFriends = List.of(user1);
//        List<User> actualCommonFriends = userController.getCommonFriends(user2.getId(), user3.getId());
//
//        assertEquals(actualSet, expectedSet);
//        assertEquals(expectedList, actualList);
//        assertEquals(expectedCommonFriends, actualCommonFriends);
//
//        userController.deleteFriend(user1.getId(), user2.getId());
//        expectedList.remove(user2);
//        actualList = userController.getUserFriends(userId);
//        assertEquals(expectedList, actualList);
//    }
//
//    @Test
//    public void shouldNotRemoveFriendsWhenFriendIdIsIncorrect() {
//        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(user2.getId(), 333));
//        String expectedMessage = "User with id \"333\" doesn't exist.";
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//    @Test
//    public void shouldNotRemoveFriendsWhenUserIdIsIncorrect() {
//        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(111, user3.getId()));
//        String expectedMessage = "User with id \"111\" doesn't exist.";
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//    @Test
//    public void shouldNotAddUserWhenUserAlreadyExists() {
//        User newUser = user1;
//        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(newUser));
//        String expectedMessage = "User with id " + newUser.getId() + " already exists";
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//    @Test
//    public void shouldAddUserWhenNameIsEmpty() {
//        User user6 = User.builder().email("example_6@email.ru").login("login_6").birthday(LocalDate.of(1994, 5, 14)).build();
//        userController.createUser(user6);
//        assertEquals(userController.getUserById(user6.getId()).getName(), userController.getUserById(user6.getId()).getLogin());
//    }
//
//    @Test
//    public void shouldUpdateUserNormal() {
//        final User updatedUser = user1;
//        updatedUser.setEmail("new@email.ru");
//        updatedUser.setLogin("newLogin");
//        updatedUser.setName("newName");
//        updatedUser.setBirthday(LocalDate.of(2020, 1, 1));
//
//        userController.updateUser(updatedUser);
//
//        User expected = updatedUser;
//        User actual = userController.getUserById(user1.getId());
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void shouldNotUpdateUserIncorrectId() {
//        User updatedUser = User.builder().id(999).build();
//        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.updateUser(updatedUser));
//        String expectedMessage = "User with id " + updatedUser.getId() + " doesn't exist";
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//    @Test
//    public void shouldGetUserWhenIdIsCorrect() {
//        User expectedUser = user1;
//        User actualUser = userController.getUserById(user1.getId());
//        assertEquals(expectedUser, actualUser);
//    }
//
//    @Test
//    public void shouldDeleteUser() {
//        User user6 = User.builder().email("example_6@email.ru").login("login_6").birthday(LocalDate.of(1994, 5, 14)).build();
//        userController.createUser(user6);
//    }
//
//    @Test
//    public void shouldNotGetUserWhenIdIsIncorrect() {
//        int userId = 999;
//        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.getUserById(userId));
//        String expectedMessage = "User with id " + userId + " doesn't exist";
//        String actualMessage = exception.getMessage();
//        assertEquals(expectedMessage, actualMessage);
//    }
//}