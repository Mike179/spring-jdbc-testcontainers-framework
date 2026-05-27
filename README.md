# Spring JDBC + Testcontainers QA Framework

## Stack

- Java 21
- Spring JDBC
- PostgreSQL
- Testcontainers
- JUnit 5
- Allure

## Как работает

Run test
↓
Testcontainers запускает PostgreSQL container
↓
Spring JDBC подключается к БД
↓
Тест выполняется
↓
Container удаляется автоматически

## Важно

Docker Desktop должен быть запущен.

Но docker compose больше не нужен.

## Запуск

1. Запустить Docker Desktop

2. Открыть проект в IntelliJ IDEA

3. Запустить CreateUserTest

## Allure

mvn clean test

allure serve target/allure-results
