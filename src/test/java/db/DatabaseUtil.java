package db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseUtil {

    public static JdbcTemplate getJdbcTemplate(
            String url,
            String username,
            String password
    ) {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource();

        dataSource.setDriverClassName(
                "org.postgresql.Driver"
        );

        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getJdbcTemplate() {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource();

        dataSource.setDriverClassName(
                "org.postgresql.Driver"
        );

        dataSource.setUrl(
                System.getenv("DB_URL")
        );

        dataSource.setUsername(
                System.getenv("DB_USER")
        );

        dataSource.setPassword(
                System.getenv("DB_PASSWORD")
        );

        return new JdbcTemplate(dataSource);
    }
}