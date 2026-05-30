package tests;

import db.DatabaseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CloudCreateUserTest {

    @Test
    void shouldCreateUserInNeon() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate();

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(200)
                )
                """);

        jdbcTemplate.update(
                "INSERT INTO users(name,email) VALUES (?,?)",
                "Mike",
                "mike@test.com"
        );

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM users",
                        Integer.class
                );

        assertEquals(
                true,
                count > 0
        );
    }
}