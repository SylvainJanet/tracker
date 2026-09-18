CREATE TABLE weight_measurement
(
    date         TEXT NOT NULL PRIMARY KEY
        CHECK (
            length(date) = 10
                AND date GLOB
                    '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                AND coalesce(
                    date = date(date, '+0 days'),
                    0
                    )
            ),

    weight_in_g INTEGER NOT NULL
        CHECK (weight_in_g > 0)

) STRICT;