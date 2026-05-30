package repository;

import org.springframework.jdbc.core.JdbcTemplate;

public class AccountRepository {

 private final JdbcTemplate jdbcTemplate;

 public AccountRepository(JdbcTemplate jdbcTemplate) {
  this.jdbcTemplate = jdbcTemplate;
 }

 public void createTable() {

  jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS accounts(
                    id SERIAL PRIMARY KEY,
                    owner VARCHAR(100),
                    balance INTEGER
                )
                """);
 }

 public void cleanTable() {

  jdbcTemplate.execute(
          "TRUNCATE TABLE accounts RESTART IDENTITY"
  );
 }

 public void insert(String owner, Integer balance) {

  jdbcTemplate.update(
          "INSERT INTO accounts(owner,balance) VALUES (?,?)",
          owner,
          balance
  );
 }

 public Integer getBalance(Long id) {

  return jdbcTemplate.queryForObject(
          "SELECT balance FROM accounts WHERE id=?",
          Integer.class,
          id
  );
 }

 public void updateBalance(Long id, Integer balance) {

  jdbcTemplate.update(
          "UPDATE accounts SET balance=? WHERE id=?",
          balance,
          id
  );
 }

 public Integer countAccounts() {

  return jdbcTemplate.queryForObject(
          "SELECT count(*) FROM accounts",
          Integer.class
  );
 }

 public String getOwner(Long id) {

  return jdbcTemplate.queryForObject(
          "SELECT owner FROM accounts WHERE id=?",
          String.class,
          id
  );
 }

 public void delete(Long id) {

  jdbcTemplate.update(
          "DELETE FROM accounts WHERE id=?",
          id
  );
 }
}