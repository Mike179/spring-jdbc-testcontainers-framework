package repository;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTable() {

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100)
                )
                """);
    }

    public void cleanTable() {

        jdbcTemplate.execute(
                "TRUNCATE TABLE users RESTART IDENTITY"
        );
    }

    public void insert(String name) {

        jdbcTemplate.update(
                "INSERT INTO users(name) VALUES (?)",
                name
        );
    }

    public Integer countUsers() {

        return jdbcTemplate.queryForObject(
                "SELECT count(*) FROM users",
                Integer.class
        );
    }

    public String getUserName(Long id) {

        return jdbcTemplate.queryForObject(
                "SELECT name FROM users WHERE id=?",
                String.class,
                id
        );
    }

    public void update(Long id, String name) {

        jdbcTemplate.update(
                "UPDATE users SET name=? WHERE id=?",
                name,
                id
        );
    }

    public void delete(Long id) {

        jdbcTemplate.update(
                "DELETE FROM users WHERE id=?",
                id
        );
    }
}