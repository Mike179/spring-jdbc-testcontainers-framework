package tests.local;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("local")
@Testcontainers
@Epic("Database tests")
@Feature("User management")
public class DeleteUserTest {

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
    @Story("Delete user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checks user deletion from PostgreSQL")
    void shouldDeleteUser() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                );

        UserRepository repository =
                new UserRepository(jdbcTemplate);

        repository.createTable();
        repository.cleanTable();

        Allure.step("Insert user Mike", () -> {
            repository.insert("Mike");
        });

        Allure.step("Delete user", () -> {
            repository.delete(1L);
        });

        Integer count = Allure.step(
                "Get users count",
                repository::countUsers
        );

        Allure.addAttachment(
                "Users count",
                String.valueOf(count)
        );

        assertEquals(0, count);
    }
}