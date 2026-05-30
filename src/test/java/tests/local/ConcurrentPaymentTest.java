package tests.local;

import db.DatabaseUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import repository.AccountRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("local")
@Testcontainers
@Epic("Database tests")
@Feature("Concurrent payments")
public class ConcurrentPaymentTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    @Story("Concurrent balance updates")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checks concurrent payment processing")
    void shouldHandleConcurrentRequests() throws Exception {

        JdbcTemplate jdbcTemplate =
                DatabaseUtil.getJdbcTemplate(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                );

        AccountRepository repository =
                new AccountRepository(jdbcTemplate);

        Allure.step("Create accounts table", () -> {
            repository.createTable();
        });

        Allure.step("Clean accounts table", () -> {
            repository.cleanTable();
        });

        Allure.step("Create account with balance 1000", () -> {
            repository.insert("Mike", 1000);
        });

        int threadCount = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch =
                new CountDownLatch(threadCount);

        Allure.step("Run 10 parallel payments", () -> {

            for (int i = 0; i < threadCount; i++) {

                executor.submit(() -> {

                    try {

                        Integer balance =
                                repository.getBalance(1L);

                        repository.updateBalance(
                                1L,
                                balance - 100
                        );

                    } finally {
                        latch.countDown();
                    }
                });
            }
        });

        latch.await();

        executor.shutdown();

        Integer finalBalance =
                repository.getBalance(1L);

        Allure.addAttachment(
                "Final balance",
                String.valueOf(finalBalance)
        );

        Allure.step("Verify final balance", () -> {

            assertEquals(
                    0,
                    finalBalance
            );
        });
    }
}