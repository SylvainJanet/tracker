package fr.sylvainjanet.tracker.hexagonalfixture.adapter.out.persistence.repository;

import org.springframework.jdbc.core.JdbcTemplate;

public final class JdbcPersistenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPersistenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
