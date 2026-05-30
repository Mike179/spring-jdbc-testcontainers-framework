package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Banking")
@Feature("Users")
public class CloudCreateUserTest {

    @Test
    @Story("Create user in Neon PostgreSQL")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that user can be created in cloud PostgreSQL database")
    void shouldCreateUserInNeon() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate();

        Allure.step("Clean users table", () -> {

            String sql =
                    "TRUNCATE TABLE users RESTART IDENTITY";

            jdbcTemplate.execute(sql);

            Allure.addAttachment(
                    "SQL",
                    sql
            );
        });

        Allure.step("Insert user Mike", () -> {

            String sql =
                    "INSERT INTO users(name,email) VALUES (?,?)";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    sql,
                    "Mike",
                    "mike@test.com"
            );

            Allure.addAttachment(
                    "Inserted user",
                    "Mike / mike@test.com"
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

                    Integer result =
                            jdbcTemplate.queryForObject(
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

        Allure.step("Verify user exists in database", () -> {

            Allure.addAttachment(
                    "Expected count",
                    ">= 1"
            );

            Allure.addAttachment(
                    "Actual count",
                    String.valueOf(count)
            );

            assertTrue(count > 0);
        });
    }
}