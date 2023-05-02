# Filmorate Project 

🎯 Project completed during study at Yandex Practicum that allows 
to store data about films and users and perform basic functions like
adding, updating, deleting films/users and also getting information 
like most popular films and lists of user friends.

## 💾 Database Structure Overview
Below is a short explanation how the database structure works
![2023-05-02 16 25 52](https://user-images.githubusercontent.com/118021621/235696186-f7e882fa-cb94-4581-8ca3-cb4b2ce81779.jpg)
## 🎥 Films

Table ```FILMS``` contains basic information about films:
* ```FILM_ID```: _primary key_, film's id
* ```FILM_NAME```: name of the film
* ```DESCRIPTION```: short description of a film
* ```DURATION```: duration of a film in minutes
* ```RATING_ID```: MPA rating of a film

### _Create film and genre tables_
SQL code to create such table:
```
CREATE TABLE FILMS (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	FILM_NAME CHARACTER VARYING(45) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER NOT NULL,
	RATING_ID INTEGER NOT NULL REFERENCES RATINGS(RATING_ID) ON DELETE CASCADE
);
```

We also want to create ```GENRES``` table that will contain possible film genres:

```
CREATE TABLE GENRES (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	GENRE_NAME CHARACTER VARYING(25) NOT NULL
);
```

Films and genres are related as _"many-to-many"_ so we want to create a
```FILMS_GENRES``` table that will contain all the possible unique combinations
by combining two _foreign keys_: ```FILM_ID``` and ```GENRE_ID```.

```
CREATE TABLE FILMS_GENRES (
	FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
	GENRE_ID INTEGER NOT NULL REFERENCES GENRES(GENRE_ID) ON DELETE CASCADE
);
```

## 👨‍💻 Users
Table ```APP_USERS``` contains all the basic information about users:
* ```USER_ID```: _primary key_, user id
* ```USER_NAME```: user name
* ```LOGIN```: user login
* ```EMAIL```: user email
* ```BIRTHDAY```: user birthday

### _Create users table_
```
CREATE TABLE APP_USERS (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
	USER_NAME CHARACTER VARYING(45),
	LOGIN CHARACTER VARYING(45) NOT NULL,
	EMAIL CHARACTER VARYING(45) NOT NULL,
	BIRTHDAY DATE NOT NULL
);
```

Users can put likes to films, which is a _"many-to-many"_ relationship, to connect 
them we need to create a ```LIKES``` table with two _foreign keys_: ```FILM_ID``` 
and ```USER_ID```.

### _Create likes table_
```
CREATE TABLE LIKES (
	FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID) ON DELETE CASCADE,
	USER_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE
);
```

## 👥 User friends list
Each user can send a friend request to another user.  

Table ```FRIENDS``` has two _foreign keys_ ```USER_ONE_ID``` and ```USER_TWO_ID```
each linked to the table ```USERS``` _primary key_ ```USER_ID```. In this table ```USER_ONE_ID```
sends request to ```USER_TWO_ID```.

### _Create friends table_
```
CREATE TABLE FRIENDS (
	USER_ONE_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE,
	USER_TWO_ID INTEGER NOT NULL REFERENCES APP_USERS(USER_ID) ON DELETE CASCADE
);
```

## _SQL queries examples_

### _SQL queries for films_

**GET all films**
```
SELECT * FROM FILMS;
```

**GET film by ID**
```
SELECT * FROM FILMS f WHERE FILM_ID = ?
```
**GET film rating**
```
SELECT r.RATING_ID, r.RATING_NAME 
FROM RATINGS r 
JOIN FILMS f ON r.RATING_ID = f.RATING_ID 
WHERE f.FILM_ID = ?;
```

**GET film genres**
```
SELECT g.GENRE_ID, g.GENRE_NAME 
FROM GENRES g 
JOIN FILMS_GENRES fg ON g.GENRE_ID = fg.GENRE_ID 
JOIN FILMS f ON fg.FILM_ID = f.FILM_ID 
WHERE f.FILM_ID = ?;
```

**Add like to film**
```
INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?);
```
**Delete like from film**
```
DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?;
```
**Get most popular films**
```
SELECT * FROM FILMS f 
LEFT JOIN 
    (SELECT l.FILM_ID, COUNT(l.USER_ID) AS LIKES_COUNT 
    FROM LIKES l 
    GROUP BY l.FILM_ID) temp ON f.FILM_ID = temp.FILM_ID 
ORDER BY temp.LIKES_COUNT DESC;
```
### _SQL queries for users_

**Get all users**
```
SELECT * FROM APP_USERS;
```

**Get user by ID**
```
SELECT * FROM APP_USERS WHERE USER_ID = ?;
```

**Add friend**
```
INSERT INTO FRIENDS (USER_ONE_ID, USER_TWO_ID) VALUES (?, ?);
```

**Delete friend**
```
"DELETE FROM FRIENDS WHERE USER_ONE_ID = ? AND USER_TWO_ID = ?";
```
**Get friends**
```
SELECT * 
FROM APP_USERS au 
JOIN FRIENDS f ON au.USER_ID = f.USER_TWO_ID 
WHERE f.USER_ONE_ID = ?;
```