package com.ricsanfre.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersTest extends AbstractTestcontainersUnitTest {

    @Test
    void canStartPostgresDB() {

        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
        // assertThat(postgreSQLContainer.isHealthy()).isTrue();
    }

}
