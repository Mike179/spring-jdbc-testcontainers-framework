package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Epic("Payments")
@Feature("Idempotency")
public class IdempotentPaymentTest {

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
 @Story("Prevent duplicate payment")
 @Severity(SeverityLevel.CRITICAL)
 @Description("Checks idempotent payment processing")
 void shouldPreventDuplicatePayment() {

  JdbcTemplate jdbcTemplate =
          DatabaseUtil.getJdbcTemplate(
                  postgres.getJdbcUrl(),
                  postgres.getUsername(),
                  postgres.getPassword()
          );

  jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS payments(
                    id SERIAL PRIMARY KEY,
                    payment_id VARCHAR(100) UNIQUE,
                    amount INTEGER
                )
                """);

  jdbcTemplate.execute(
          "TRUNCATE TABLE payments RESTART IDENTITY"
  );

  String paymentId = "PAY-001";

  Allure.step("Create first payment", () -> {
   jdbcTemplate.update(
           "INSERT INTO payments(payment_id, amount) VALUES (?, ?)",
           paymentId,
           1000
   );
  });

  Allure.step("Try duplicate payment", () -> {

   Integer count = jdbcTemplate.queryForObject(
           "SELECT count(*) FROM payments WHERE payment_id=?",
           Integer.class,
           paymentId
   );

   if (count == 0) {
    jdbcTemplate.update(
            "INSERT INTO payments(payment_id, amount) VALUES (?, ?)",
            paymentId,
            1000
    );
   }
  });

  Integer totalPayments =
          jdbcTemplate.queryForObject(
                  "SELECT count(*) FROM payments",
                  Integer.class
          );

  Allure.addAttachment(
          "Payments count",
          String.valueOf(totalPayments)
  );

  assertEquals(1, totalPayments);
 }
}