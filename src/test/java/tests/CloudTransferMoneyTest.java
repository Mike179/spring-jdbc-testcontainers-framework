package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Banking")
@Feature("Transfers")
public class CloudTransferMoneyTest {

    @Test
    @Story("Transfer money between accounts")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful money transfer between two accounts in Neon PostgreSQL")
    void shouldTransferMoney() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate();

        Allure.step("Clean accounts table", () -> {

            String sql =
                    "TRUNCATE TABLE accounts RESTART IDENTITY";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.execute(sql);
        });

        Allure.step("Create sender account", () -> {

            String sql =
                    "INSERT INTO accounts(owner,balance) VALUES (?,?)";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    sql,
                    "Mike",
                    1000
            );
        });

        Allure.step("Create receiver account", () -> {

            String sql =
                    "INSERT INTO accounts(owner,balance) VALUES (?,?)";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    sql,
                    "John",
                    500
            );
        });

        Allure.step("Transfer 200 from Mike to John", () -> {

            jdbcTemplate.update(
                    "UPDATE accounts SET balance = balance - 200 WHERE id = 1"
            );

            jdbcTemplate.update(
                    "UPDATE accounts SET balance = balance + 200 WHERE id = 2"
            );

            Allure.addAttachment(
                    "Transfer amount",
                    "200"
            );
        });

        Integer mikeBalance = Allure.step(
                "Get sender balance",
                () -> {

                    Integer result =
                            jdbcTemplate.queryForObject(
                                    "SELECT balance FROM accounts WHERE id=1",
                                    Integer.class
                            );

                    Allure.addAttachment(
                            "Mike balance",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        Integer johnBalance = Allure.step(
                "Get receiver balance",
                () -> {

                    Integer result =
                            jdbcTemplate.queryForObject(
                                    "SELECT balance FROM accounts WHERE id=2",
                                    Integer.class
                            );

                    Allure.addAttachment(
                            "John balance",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        Allure.step("Verify balances after transfer", () -> {

            Allure.addAttachment(
                    "Expected Mike balance",
                    "800"
            );

            Allure.addAttachment(
                    "Actual Mike balance",
                    String.valueOf(mikeBalance)
            );

            Allure.addAttachment(
                    "Expected John balance",
                    "700"
            );

            Allure.addAttachment(
                    "Actual John balance",
                    String.valueOf(johnBalance)
            );

            assertEquals(800, mikeBalance);
            assertEquals(700, johnBalance);
        });
    }
}