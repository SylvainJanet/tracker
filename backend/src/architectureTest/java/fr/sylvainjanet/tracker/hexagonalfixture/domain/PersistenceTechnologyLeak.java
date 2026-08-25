package fr.sylvainjanet.tracker.hexagonalfixture.domain;

import org.springframework.jdbc.core.JdbcTemplate;

public final class PersistenceTechnologyLeak {

    private final JdbcTemplate jdbcTemplate;

    public PersistenceTechnologyLeak(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
