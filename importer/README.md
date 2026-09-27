# Weight importer

The importer is a standalone Java application. It does not depend on the
backend module or access SQLite directly.

## TRACKER-24 behavior

This version validates the complete CSV and prints a safe summary. It does not
send HTTP requests or modify Tracker data. API delivery is implemented by
TRACKER-25.

The `--base-url` argument is validated now so the command-line contract remains
stable when API delivery is added.

## Source CSV

Export the spreadsheet as its original UTF-8, semicolon-delimited CSV.

- Keep the exact `Date` and `Weight` headers.
- Other columns, including unnamed columns, are ignored.
- Dates use `dd/MM/yy`; years are interpreted as 2000–2099.
- Weights use kilograms with a decimal comma.
- Blank weights are skipped.
- Nonblank weights after the `--through` date are skipped and counted.
- Duplicate dates and malformed values reject the complete file.

Keep real exports below the ignored `data/` directory. Never commit personal
exports or derive automated-test fixtures from them.

## Validate a synthetic export

From the repository root:

```bash
./gradlew :importer:run --args="\
--input importer/src/test/resources/synthetic-weight-export.csv \
--base-url http://127.0.0.1:8080 \
--through 2025-11-03"
```

The command reports source-row, accepted-measurement, skipped-blank and
skipped-after-cutoff counts without printing weight values.

## Validate a real export

Place the export below `data/`, confirm the last genuinely measured date, then
run:

```bash
./gradlew :importer:run --args="\
--input data/weight-export.csv \
--base-url http://127.0.0.1:8080 \
--through YYYY-MM-DD"
```

A successful TRACKER-24 run proves only that the source is valid. It does not
prove that any measurement was imported.
