package com.example.oidtypeconversion;

import com.example.oidtypeconversion.aggregate.SimpleCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void it_should_store_payload_as_bytea() {
        // When
        SimpleCommand simpleCommand = new SimpleCommand("test");
        commandGateway.sendAndWait(simpleCommand);

        // Then
        String sql = "SELECT payload FROM domain_event_entry";
        jdbcTemplate.query(sql, rs -> {
            String columnType = rs.getMetaData().getColumnTypeName(1);
            assertThat(columnType).isEqualToIgnoringCase("bytea");

            byte[] payload = rs.getBytes(1);
            byte[] expected = "<com.example.oidtypeconversion.aggregate.SimpleEvent><id>test</id></com.example.oidtypeconversion.aggregate.SimpleEvent>"
                    .getBytes(StandardCharsets.UTF_8);
            assertThat(payload).isEqualTo(expected);
        });
    }
}
