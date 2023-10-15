package com.ricsanfre.demo;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestcontainersUnitTest {

    protected static final Faker FAKER = new Faker();

    protected static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAll() {
        // Configure Testcontainers DB using Flyway
        // Flyway Java API https://documentation.red-gate.com/fd/api-java-184127629.html
        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword())
                .load();
        flyway.migrate();

        jdbcTemplate = buildJdbcTemplate();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("demo-dao-unit-test")
                    .withUsername("ricsanfre")
                    .withPassword("s1cret0");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                ()-> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username",
                ()-> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password",
                ()-> postgreSQLContainer.getPassword());

    }

    private static DataSource getDataSource() {
        // Building datasource
        DataSourceBuilder builder = DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());
        return builder.build();
    }

    private static JdbcTemplate buildJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
