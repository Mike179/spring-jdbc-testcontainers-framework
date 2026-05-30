package tests;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@Epic("Database tests")
@Feature("Transactions")
public class RollbackTransactionTest {

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
 @Story("Rollback transaction")
 @Severity(SeverityLevel.CRITICAL)
 @Description("Checks transaction rollback")
 void shouldRollback() {

  var dataSource =
          new org.springframework.jdbc.datasource.DriverManagerDataSource(
                  postgres.getJdbcUrl(),
                  postgres.getUsername(),
                  postgres.getPassword()
          );

  JdbcTemplate jdbcTemplate =
          new JdbcTemplate(dataSource);

  jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS accounts(
                    id SERIAL PRIMARY KEY,
                    owner VARCHAR(100),
                    balance INTEGER
                )
                """);

  jdbcTemplate.execute(
          "TRUNCATE TABLE accounts RESTART IDENTITY"
  );

  jdbcTemplate.update(
          "INSERT INTO accounts(owner,balance) VALUES (?,?)",
          "Mike",
          1000
  );

  DataSourceTransactionManager txManager =
          new DataSourceTransactionManager(dataSource);

  TransactionStatus tx =
          txManager.getTransaction(
                  new DefaultTransactionDefinition()
          );

  try {

   jdbcTemplate.update(
           "UPDATE accounts SET balance=800 WHERE id=1"
   );

   throw new RuntimeException(
           "Payment processing failed"
   );

  } catch (Exception e) {

   txManager.rollback(tx);

   Allure.addAttachment(
           "Rollback reason",
           e.getMessage()
   );
  }

  Integer balance =
          jdbcTemplate.queryForObject(
                  "SELECT balance FROM accounts WHERE id=1",
                  Integer.class
          );

  Allure.addAttachment(
          "Balance after rollback",
          String.valueOf(balance)
  );

  assertEquals(1000, balance);
 }
}