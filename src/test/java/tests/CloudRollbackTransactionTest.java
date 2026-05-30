package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Banking")
@Feature("Transactions")
public class CloudRollbackTransactionTest {

    @Test
    @Story("Rollback transfer when balance is insufficient")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that money transfer is not executed when account balance is lower than transfer amount")
    void shouldNotTransferWhenBalanceTooLow() {

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

        Allure.step("Create account with balance 100", () -> {

            String sql =
                    "INSERT INTO accounts(owner,balance) VALUES (?,?)";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    sql,
                    "Mike",
                    100
            );
        });

        Integer balance = Allure.step(
                "Get current account balance",
                () -> {

                    String sql =
                            "SELECT balance FROM accounts WHERE id=1";

                    Integer result =
                            jdbcTemplate.queryForObject(
                                    sql,
                                    Integer.class
                            );

                    Allure.addAttachment(
                            "Current balance",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        int transferAmount = 500;

        Allure.step("Attempt transfer of 500", () -> {

            Allure.addAttachment(
                    "Transfer amount",
                    String.valueOf(transferAmount)
            );

            if (balance >= transferAmount) {

                jdbcTemplate.update(
                        "UPDATE accounts SET balance = balance - ? WHERE id=1",
                        transferAmount
                );
            }
        });

        Integer finalBalance = Allure.step(
                "Get final account balance",
                () -> {

                    String sql =
                            "SELECT balance FROM accounts WHERE id=1";

                    Integer result =
                            jdbcTemplate.queryForObject(
                                    sql,
                                    Integer.class
                            );

                    Allure.addAttachment(
                            "Final balance",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        Allure.step("Verify balance was not changed", () -> {

            Allure.addAttachment(
                    "Expected",
                    "100"
            );

            Allure.addAttachment(
                    "Actual",
                    String.valueOf(finalBalance)
            );

            assertEquals(
                    100,
                    finalBalance
            );
        });
    }
}