package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Epic("Database tests")
@Feature("User management")
public class UpdateUserTest {

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
    @Story("Update user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checks user update in PostgreSQL")
    void shouldUpdateUser() {

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

        Allure.step("Create user Mike", () -> {
            repository.insert("Mike");
        });

        Allure.step("Update Mike to John", () -> {
            repository.update(
                    1L,
                    "John"
            );
        });

        String actualName = Allure.step(
                "Get updated user",
                () -> repository.getUserName(1L)
        );

        Allure.addAttachment(
                "Actual name",
                actualName
        );

        assertEquals(
                "John",
                actualName
        );
    }
}