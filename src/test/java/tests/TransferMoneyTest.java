package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Epic("Payments")
@Feature("Money transfer")
public class TransferMoneyTest {

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
 @Story("Transfer money between accounts")
 @Severity(SeverityLevel.CRITICAL)
 @Description("Checks money transfer between two accounts")
 void shouldTransferMoney() {

  JdbcTemplate jdbcTemplate =
          DatabaseUtil.getJdbcTemplate(
                  postgres.getJdbcUrl(),
                  postgres.getUsername(),
                  postgres.getPassword()
          );

  AccountRepository repository =
          new AccountRepository(jdbcTemplate);

  repository.createTable();
  repository.cleanTable();

  Allure.step("Create source account", () -> {
   repository.insert("Mike", 1000);
  });

  Allure.step("Create destination account", () -> {
   repository.insert("John", 500);
  });

  Allure.step("Transfer 200 from Mike to John", () -> {

   Integer mikeBalance =
           repository.getBalance(1L);

   Integer johnBalance =
           repository.getBalance(2L);

   repository.updateBalance(
           1L,
           mikeBalance - 200
   );

   repository.updateBalance(
           2L,
           johnBalance + 200
   );
  });

  Integer mikeFinalBalance =
          repository.getBalance(1L);

  Integer johnFinalBalance =
          repository.getBalance(2L);

  Allure.addAttachment(
          "Mike balance",
          String.valueOf(mikeFinalBalance)
  );

  Allure.addAttachment(
          "John balance",
          String.valueOf(johnFinalBalance)
  );

  assertEquals(
          800,
          mikeFinalBalance
  );

  assertEquals(
          700,
          johnFinalBalance
  );
 }
}