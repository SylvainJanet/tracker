# Weight importer

The importer is a standalone Java application. It does not depend on the
backend module or access SQLite directly. It validates the complete CSV, then
sends each accepted measurement sequentially through Tracker's existing Journal
HTTP API.

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

The complete source is validated before the first HTTP request. Accepted rows
are sent to `POST /api/journal/weight-measurement`. The summary reports source,
skipped, successful and failed counts without printing weight values.

## Import synthetic data

From the repository root, start Tracker with an empty development database:

```bash
./gradlew :backend:bootRunEmpty
```

In another terminal, run:

```bash
./gradlew :importer:run --args="\
--input importer/src/test/resources/synthetic-weight-export.csv \
--base-url http://127.0.0.1:8080 \
--through 2025-11-03"
```

Verify representative imported measurements through the API:

```bash
curl --fail --silent \
  http://127.0.0.1:8080/api/journal/weight-measurement/2025-11-01

curl --fail --silent \
  http://127.0.0.1:8080/api/journal/weight-measurement/2025-11-03
```

## Import a real export

Before importing into the personal database:

1. Confirm the export contains the exact `Weight` and `Date` headers.
2. Place it below the ignored `data/` directory without removing or rearranging
   columns.
3. Confirm the last genuinely measured date for `--through`.
4. Retain or create a user-approved recoverable backup.
5. Test the import first with an empty or copied development database.
6. Check representative first, middle and last dates through the GET endpoint.

Run the importer from the repository root:

```bash
./gradlew :importer:run --args="\
--input data/legacy-spreadsheet.csv \
--base-url http://127.0.0.1:8080 \
--through YYYY-MM-DD"
```

Use `./gradlew :backend:bootRunReal` only after explicit authorization to modify
the personal database and after confirming the backup is recoverable.

## Failure and recovery

An HTTP failure stops the importer and returns a nonzero exit status. Requests
that succeeded before the failure remain stored, so an import can be partial.

After correcting the failure, rerun the same command. The Journal endpoint
replaces measurements by date, making a rerun the recovery mechanism without
creating duplicate dates. Blank or absent source rows do not delete existing
measurements.
