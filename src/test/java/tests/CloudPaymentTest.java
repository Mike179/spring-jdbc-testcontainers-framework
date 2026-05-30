package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Banking")
@Feature("Payments")
public class CloudPaymentTest {

    @Test
    @Story("Create payment in Neon PostgreSQL")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that payment record can be created in cloud PostgreSQL database")
    void shouldCreatePayment() {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate();

        Allure.step("Clean payments table", () -> {

            String sql =
                    "TRUNCATE TABLE payments RESTART IDENTITY";

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.execute(sql);
        });

        Allure.step("Create payment record", () -> {

            String sql = """
                    INSERT INTO payments(
                        from_account,
                        to_account,
                        amount,
                        status,
                        idempotency_key
                    )
                    VALUES (?,?,?,?,?)
                    """;

            Allure.addAttachment(
                    "SQL",
                    sql
            );

            jdbcTemplate.update(
                    sql,
                    1,
                    2,
                    500,
                    "SUCCESS",
                    "PAYMENT-001"
            );

            Allure.addAttachment(
                    "Payment",
                    """
                    from_account=1
                    to_account=2
                    amount=500
                    status=SUCCESS
                    idempotency_key=PAYMENT-001
                    """
            );
        });

        Integer count = Allure.step(
                "Get payments count",
                () -> {

                    String sql =
                            "SELECT count(*) FROM payments";

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

        Integer paymentId = Allure.step(
                "Get created payment id",
                () -> {

                    String sql =
                            "SELECT id FROM payments WHERE idempotency_key = ?";

                    Allure.addAttachment(
                            "SQL",
                            sql
                    );

                    Integer result =
                            jdbcTemplate.queryForObject(
                                    sql,
                                    Integer.class,
                                    "PAYMENT-001"
                            );

                    Allure.addAttachment(
                            "Payment ID",
                            String.valueOf(result)
                    );

                    return result;
                }
        );

        Allure.step("Verify payment was created", () -> {

            Allure.addAttachment(
                    "Expected count",
                    "1"
            );

            Allure.addAttachment(
                    "Actual count",
                    String.valueOf(count)
            );

            assertEquals(1, count);
            assertNotNull(paymentId);
        });
    }
}