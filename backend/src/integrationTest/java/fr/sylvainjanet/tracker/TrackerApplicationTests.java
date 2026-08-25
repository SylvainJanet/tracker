package fr.sylvainjanet.tracker;

import static org.assertj.core.api.Assertions.assertThat;

import fr.sylvainjanet.tracker.configuration.sqlite.environment.SqliteTestDatabase;
import fr.sylvainjanet.tracker.configuration.sqlite.environment.TestSqliteDatabase;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SqliteTestDatabase("tracker-application")
@SpringBootTest
class TrackerApplicationTests {

    @Autowired MockMvc mockMvc;

    @Autowired ServerProperties serverProperties;

    @Autowired DataSource dataSource;

    @Test
    void contextLoads() {}

    @Test
    void usesAnnotatedTestDatabase(TestSqliteDatabase database) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL()).isEqualTo(database.jdbcUrl());
        }
    }

    @Test
    void bindsServerToLoopbackInterface() {
        assertThat(serverProperties.getAddress()).isNotNull();
        assertThat(serverProperties.getAddress().isLoopbackAddress()).isTrue();
    }
}
