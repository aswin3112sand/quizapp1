# QuizApp

Online quiz platform built with Spring Boot, Thymeleaf, and Spring Security. It ships with a seeded "General Knowledge" quiz, user authentication, an admin question manager, and a real-time leaderboard over WebSockets.

## Features
- Timed quiz flow with instant scoring and results page
- User registration/login with role-based access (USER/ADMIN)
- Admin dashboard to create/edit/delete questions
- Real-time leaderboard (STOMP over WebSocket + SockJS)
- Personal score history for logged-in users
- H2 in-memory database by default, with MySQL/PostgreSQL options

## Tech Stack
- Java 17, Spring Boot 3.3.x
- Spring MVC + Thymeleaf
- Spring Data JPA (Hibernate)
- Spring Security
- WebSockets (STOMP + SockJS)
- H2 / MySQL / PostgreSQL

## Quick Start (Local)
Prereqs: Java 17, Maven 3.9+

```bash
mvn spring-boot:run
```

App runs at: `http://localhost:8080`

H2 console (dev profile): `http://localhost:8080/h2`  
JDBC URL: `jdbc:h2:mem:quizapp`  
User: `sa` (empty password)

## Default Admin (Seeded)
On startup (all profiles except `test`), the app seeds an admin user:

- Email: `admin3112@gmail.com`
- Password: `nextnext`

Change this for production by updating `src/main/java/com/example/quizapp/QuizAppApplication.java`
or updating the user in the database.

## Configuration
By default, the app uses H2. You can override with env vars:

- `SPRING_PROFILES_ACTIVE=dev|mysql|prod`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`

MySQL profile example:

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_URL=jdbc:mysql://localhost:3306/quizapp \
DB_USERNAME=root \
DB_PASSWORD=your_password \
mvn spring-boot:run
```

## Docker

```bash
docker build -t quizapp .
docker run -p 8080:8080 quizapp
```

## Useful Routes
- `/` home
- `/quiz` take quiz
- `/leaderboard` leaderboard page (updates via WebSocket)
- `/api/leaderboard` leaderboard JSON
- `/me/scores` logged-in user history
- `/admin` admin dashboard

## Tests

```bash
mvn test
```
