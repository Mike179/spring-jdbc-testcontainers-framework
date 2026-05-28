package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Epic("Database tests")
@Feature("Spring JDBC + Testcontainers")
public class CreateUserTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .waitingFor(
                            Wait.forListeningPort()
                    );

    @Test
    @Story("Create user in PostgreSQL")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checks user creation using Testcontainers")
    void shouldCreateUser() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                );

        Allure.step("Create users table", () -> {

            String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100)
                    )
                    """;

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.execute(sql);

            Allure.addAttachment(
                    "Result",
                    "Users table created successfully"
            );
        });

        Allure.step("Clean users table", () -> {
            String sql =
                    "TRUNCATE TABLE users RESTART IDENTITY";
            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.execute(sql);

            Allure.addAttachment(
                    "Result",
                    "Users table cleaned"
            );
        });

        Allure.step("Insert user Mike", () -> {
            String sql =
                    "INSERT INTO users(name) VALUES ('Mike')";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    "INSERT INTO users(name) VALUES (?)",
                    "Mike"
            );

            Allure.addAttachment(
                    "Inserted user",
                    "Mike"
            );
        });

        Integer count = Allure.step(
                "Get users count",
                () -> {

                    String sql =
                            "SELECT count(*) FROM users";

                    Allure.addAttachment(
                            "SQL",
                            sql
                    );

                    Integer result = jdbcTemplate.queryForObject(
                            sql,
                            Integer.class
                    );

                    Allure.addAttachment(
                            "DB Result",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        Allure.step("Verify users count equals 1", () -> {

            Allure.addAttachment(
                    "Expected",
                    "1"
            );

            Allure.addAttachment(
                    "Actual",
                    String.valueOf(count)
            );

            assertEquals(1, count);
        });
    }
}