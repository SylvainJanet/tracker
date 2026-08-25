CREATE TABLE tracking_daily_record
(
    record_date TEXT NOT NULL PRIMARY KEY
        CHECK (
            length(record_date) = 10
                AND record_date GLOB
        '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
        AND coalesce (
        record_date = date (record_date, '+0 days'),
        0
        )
) ,

    completion_status TEXT NOT NULL
        CHECK (
            completion_status IN ('IN_PROGRESS', 'COMPLETED')
        )
) STRICT;