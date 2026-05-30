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

        Allure.step("Create sender account Mike", () -> {

            String sql =
                    "INSERT INTO accounts(owner,balance) VALUES (?,?)";

            jdbcTemplate.update(
                    sql,
                    "Mike",
                    1000
            );

            Allure.addAttachment(
                    "Account",
                    "Mike / balance = 1000"
            );
        });

        Allure.step("Create receiver account John", () -> {

            String sql =
                    "INSERT INTO accounts(owner,balance) VALUES (?,?)";

            jdbcTemplate.update(
                    sql,
                    "John",
                    500
            );

            Allure.addAttachment(
                    "Account",
                    "John / balance = 500"
            );
        });

        int transferAmount = 200;

        Allure.step("Transfer money between accounts", () -> {

            Allure.addAttachment(
                    "Transfer amount",
                    String.valueOf(transferAmount)
            );

            jdbcTemplate.update(
                    "UPDATE accounts SET balance = balance - ? WHERE id = 1",
                    transferAmount
            );

            jdbcTemplate.update(
                    "UPDATE accounts SET balance = balance + ? WHERE id = 2",
                    transferAmount
            );
        });

        Integer mikeBalance = Allure.step(
                "Get Mike balance",
                () -> jdbcTemplate.queryForObject(
                        "SELECT balance FROM accounts WHERE id=1",
                        Integer.class
                )
        );

        Integer johnBalance = Allure.step(
                "Get John balance",
                () -> jdbcTemplate.queryForObject(
                        "SELECT balance FROM accounts WHERE id=2",
                        Integer.class
                )
        );

        Allure.step("Verify transfer results", () -> {

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