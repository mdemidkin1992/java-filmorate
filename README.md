# Filmorate Project 

🎯 Project completed during study at Yandex Practicum that allows 
to store data about films and users and perform basic functions like
adding, updating, deleting films/users and also getting information 
like most popular films and lists of user friends.

<!-- TOC -->
* [💾 Database Structure Overview](#-database-structure-overview)
* [🎥 Films](#-films)
  * [_Create film and genre tables_](#_create-film-and-genre-tables_)
  * [_Data provided for films and genres_](#_data-provided-for-films-and-genres_)
  * [_SQL queries for films_](#_sql-queries-for-films_)
* [👨‍💻 Users](#-users)
  * [_Create users table_](#_create-users-table_)
  * [_Create likes table_](#_create-likes-table_)
  * [_SQL queries with users table_](#_sql-queries-with-users-table_)
* [👥 User friends list](#-user-friends-list)
  * [_Create friends table_](#_create-friends-table_)
  * [_SQL queries with friends table_](#_sql-queries-with-friends-table_)
<!-- TOC -->

## 💾 Database Structure Overview
Below is a short explanation how the database structure works
![Filmorate_ER](https://user-images.githubusercontent.com/118021621/232250639-727fc1fe-7f07-4f65-aef9-ffa1a28daab3.jpg)

## 🎥 Films

Table ```film``` contains basic information about films:
* ```film_id```: _primary key_, film's id
* ```name```: name of the film
* ```description```: short description of a film
* ```duration```: duration of a film in minutes
* ```rating```: MPA rating of a film

### _Create film and genre tables_
SQL code to create such table:
```
CREATE TABLE film (
   film_id       SERIAL PRIMARY KEY, 
   name          VARCHAR(45),
   description   VARCHAR(200),
   release_date  DATE,
   duration      INT,
   rating        VARCHAR(5)
);
```

We also want to create ```genre``` table that will contain possible film genres:

```
CREATE TABLE genre (
  genre_id      SERIAL PRIMARY KEY, 
  name          VARCHAR(25)
);
```

Films and genres are related as _"many-to-many"_ so we want to create a
```film_genre``` table that will contain all the possible unique combinations
by combining two _foreign keys_: ```film_id``` and ```genre_id```.

```
  CREATE TABLE film_genre (
    film_id       int REFERENCES film (film_id) ON UPDATE CASCADE ON DELETE CASCADE, 
    genre_id	int REFERENCES genre (genre_id) ON UPDATE CASCADE, 
    CONSTRAINT    film_genre_pkey PRIMARY KEY (film_id, genre_id)  -- explicit pk
  );
```

### _Data provided for films and genres_

Suppose the data we provided looks like this:

* ```film```

| film_id | name        | description                            | release_date | duration | rating |
|---------|-------------|----------------------------------------|--------------|----------|--------|
| 1       | Titanic     | Nothing on Earth can separate them     | 1997-11-01   | 194      | PG-13  |
| 2       | Avatar      | This is the new world                  | 2009-12-17   | 162      | PG-13  |
| 3       | Fight Club  | Intrigue. Chaos. Soap                  | 1999-09-11   | 139      | R      |
| 4       | Paddington  | Please look after this bear. Thank you | 2014-11-23   | 95       | PG     |
| 5       | The Holiday | Cheerful love comedy                   | 2006-12-07   | 136      | PG-13  |

* ```genre```

| genre_id | name           |
|----------|----------------|
| 1        | Комедия        |
| 2        | Драма          |
| 3        | Мультфильм     |
| 4        | Триллер        |
| 5        | Документальный |
| 6        | Боевик         |

* ```film_genre```

| film_id | genre_id |
|---------|----------|
| 1       | 2        |
| 2       | 2        |
| 2       | 6        |
| 3       | 4        |
| 3       | 6        |
| 4       | 1        |
| 4       | 3        |
| 1       | 5        |
| 5       | 1        |
| 5       | 2        |

### _SQL queries for films_
To match films and genres we use ```LEFT JOIN``` to add ```film``` and ```genre``` 
to ```film_genre``` (or use ```RIGHT JOIN``` but with ```film_genre``` table being 
on the right).

Now we can perform some basic actions with our database.

**Example 1: Get all films with genre "Comedy"**
```
SELECT  f.name
FROM    film_genre AS fg
LEFT    JOIN film as f ON fg.film_id=f.film_id
LEFT    JOIN genre AS g ON fg.genre_id=g.genre_id
WHERE   g.name = 'Комедия';
```
```
Output: ["Paddington", "The Holiday"]
```

**Example 2: Get genres of a film "Fight Club"**
```
SELECT      g.name
FROM        film_genre AS fg
LEFT JOIN   film as f ON fg.film_id=f.film_id
LEFT JOIN   genre AS g ON fg.genre_id=g.genre_id
WHERE       f.name = 'Fight Club';
```
```
Output:     ["Триллер", "Боевик"]
```

**Example 3: Get number of films in each genre**
```
SELECT      g.name,
            COUNT(f.name) AS film_count
FROM        film_genre AS fg
LEFT JOIN   film as f ON fg.film_id=f.film_id
LEFT JOIN   genre AS g ON fg.genre_id=g.genre_id
GROUP BY    g.name
ORDER BY    film_count DESC;
```
```
Output:     ["Драма" : "3", "Комедия" : "2", "Боевик" : "2", 
"Мультфильм" : "1", "Триллер": "1", "Документальный": "1"]
```

**Example 4: Get number of films in each rating**
```
SELECT      rating, COUNT(name) as film_count
FROM        film
GROUP BY    rating
ORDER BY    film_count DESC;
```
```
Output:     ["PG-13": "3", "R": "1", "PG": "1"]
```
**Example 5: Get list of films with release date after 2000**
```
SELECT      name
FROM        film
WHERE       EXTRACT(YEAR from release_date) > 2000;
```
```
Output:     ["Avatar", "Paddington", "The Holiday"]
```

## 👨‍💻 Users
Table ```user``` contains all the basic information about users:
* ```user_id```: _primary key_, user id
* ```name```: user name
* ```login```: user login
* ```email```: user email
* ```birthday```: user birthday

### _Create users table_
```
CREATE TABLE users (
    user_id     SERIAL PRIMARY KEY,
    name        VARCHAR(45),
    email       VARCHAR(45),
    login       VARCHAR(45),
    birthday    DATE
);
```

Users can put likes to films, which is a _"many-to-many"_ relationship, to connect 
them we need to create a ```likes``` table with two _foreign keys_: ```film_id``` 
and ```user_id```.

### _Create likes table_
```
CREATE TABLE likes (
  film_id    int REFERENCES film (film_id) ON UPDATE CASCADE ON DELETE CASCADE
, user_id	 int REFERENCES users (user_id) ON UPDATE CASCADE
, CONSTRAINT film_user_pkey PRIMARY KEY (film_id, user_id)
);
```

### _Data for users and likes tables_

* ```users```

Suppose we have 5 users with the following information:

| user_id | name  | email           | login      | birthday   |
|---------|-------|-----------------|------------|------------|
| 1       | Mark  | mark@email.com  | marklogin  | 1992-01-02 |
| 2       | Ben   | ben@email.com   | benlogin   | 1995-02-04 |
| 3       | Clark | clark@email.com | clarklogin | 1997-04-06 |
| 4       | Joe   | joe@email.com   | joelogin   | 2000-06-10 |
| 5       | John  | john@email.com  | johnlogin  | 2001-08-12 |

* ```likes```

Suppose likes are distributed by the following films by users:

| film_id | user_id |
|---------|---------|
| 1       | 1       |
| 1       | 2       |
| 1       | 3       |
| 2       | 1       |
| 2       | 3       |
| 2       | 4       |
| 2       | 5       |
| 3       | 5       |
| 4       | 1       |
| 4       | 3       |


### _SQL queries with users table_
**Example 1: Get 3 most popular films**
```
SELECT      f.name, 
            COUNT(l.user_id) as likes_count
FROM        likes AS l 
LEFT JOIN   users AS u ON u.user_id=l.user_id
LEFT JOIN   film AS f on f.film_id=l.film_id
GROUP by    f.name
ORDER BY    likes_count DESC
LIMIT       3;
```
```
Output:     ["Avatar": "4", "Titanic": "3", "Paddington": "2"]
```

**Example 2: Get film likes of a specific user**
```
SELECT      f.name
FROM        likes AS l 
LEFT JOIN   users AS u ON u.user_id=l.user_id
LEFT JOIN   film AS f on f.film_id=l.film_id
WHERE       u.name = 'Mark';
```
```
Output:     ["Titanic", "Avatar", "Paddington"]
```

**Example 3: Get user likes of a specific film**
```
SELECT      u.name, 
            u.email, 
            u.login
FROM        likes AS l 
LEFT JOIN   users AS u ON u.user_id=l.user_id
LEFT JOIN   film AS f on f.film_id=l.film_id
WHERE       f.name = 'Avatar';
```
```
Output:     ("Mark",    "mark@email.com",   "marklogin"), 
            ("Clark",   "clark@email.com",  "clarklogin"),
            ("Joe",     "joe@email.com",    "joelogin"),
            ("John",    "john@email.com",   "johnlogin")
```

## 👥 User friends list
Each user can send a friend request to another user.  

Table ```friends``` has two _foreign keys_ ```user_one_id``` and ```user_two_id```
each linked to the table ```users``` _primary key_ ```user_id```. In this table ```user_one_id```
sends request to ```user_two_id```. Additional field ```request_status``` can either
have "pending approval" (```0```) or "approved" (```1```) status. 

### _Create friends table_
```
CREATE TYPE status AS ENUM ('0', '1');
CREATE TABLE friends (
	user_one_id INT,
	user_two_id INT,
	request_status status,
PRIMARY KEY (user_one_id, user_two_id),
FOREIGN KEY (user_one_id) REFERENCES users(user_id),
FOREIGN KEY (user_two_id) REFERENCES users(user_id));
```

### _Data for friends table_

Suppose the friends lists are distributed as follows

| user_one_id | user_two_id | request_status |
|-------------|-------------|----------------|
| 1           | 2           | 0              |
| 1           | 3           | 1              |
| 2           | 3           | 0              |
| 2           | 4           | 1              |
| 2           | 5           | 0              |
| 3           | 4           | 0              |
| 4           | 1           | 1              |
| 4           | 5           | 0              |
| 5           | 1           | 1              |


### _SQL queries with friends table_
**Example 1: Get friends list of a user**
```
SELECT      u.name
FROM        friends AS f
LEFT JOIN   users AS u ON f.user_two_id=user_id
WHERE       f.user_one_id = 1 AND f.request_status = '1'
UNION
SELECT      u.name
FROM        friends AS f
LEFT JOIN   users AS u ON f.user_one_id=user_id
WHERE       f.user_two_id = 1 AND f.request_status = '1';
```
```
Output: ["Clark", "Joe", "John"]
```

**Example 2: Get common friends of two users**
```
SELECT      u.name
FROM        friends AS f
LEFT JOIN   users AS u ON f.user_two_id=user_id
WHERE       f.user_one_id = 2 AND f.request_status = '1' AND u.name IN 
(
  	SELECT      u.name
	FROM        friends AS f
	LEFT JOIN   users AS u ON f.user_two_id=user_id
	WHERE       f.user_one_id = 1 and f.request_status = '1'
	UNION
	SELECT      u.name
	FROM        friends AS f
	LEFT JOIN   users AS u ON f.user_one_id=user_id
	WHERE       f.user_two_id = 1 AND f.request_status = '1'
)
UNION
SELECT      u.name
FROM        friends AS f
LEFT JOIN   users AS u ON f.user_one_id=user_id
WHERE       f.user_two_id = 2 AND f.request_status = '1' AND u.name IN 
(
  (
  	SELECT      u.name
	FROM        friends AS f
	LEFT JOIN   users AS u ON f.user_two_id=user_id
	WHERE       f.user_one_id = 1 AND f.request_status = '1'
	UNION
	SELECT      u.name
	FROM        friends AS f
	LEFT JOIN   users AS u ON f.user_one_id=user_id
	WHERE       f.user_two_id = 1 AND f.request_status = '1'
)
);
```
```
Output: ["Joe"] --user_id=1 and user_id=2 common friends
```