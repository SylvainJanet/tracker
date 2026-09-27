# TRACKER-8 — Import weight data

## Objective

Import historical measured weights from the spreadsheet into Tracker without adding a UI or coupling the importer to SQLite.

```text
Spreadsheet
  → measured-weight CSV export
  → standalone Java importer
  → existing Journal HTTP API
  → Journal domain validation
  → SQLite
```

This is the first, deliberately limited slice of the broader Import context. It must not attempt to solve future nutrition, activity, provenance, or reconciliation requirements.

## Scope

- Import measured weights with their calendar dates.
- Use a small command-line Java tool.
- Send measurements through the existing `POST /api/journal/weight-measurement` endpoint.
- Keep the importer outside the backend production artifact.
- Support approximately 300 rows through sequential requests.
- Provide safe diagnostics and a final summary without exposing weight values.
- Test exclusively with synthetic data.

## Out of scope

- Angular changes or an import UI.
- Direct SQLite access, schema changes, or Flyway migrations.
- Reading `.xlsx` or `.ods` files directly.
- Importing predicted, interpolated, carried, or expected weights.
- Nutrition, activity, goals, day types, or completion state.
- A generic import framework.
- Bulk or transactional API endpoints.
- Import-run persistence, row fingerprints, provenance, conflict reconciliation, or deletion synchronization.

Those capabilities remain deferred to the complete historical-data import.

## Working decisions

### Source format

Read the spreadsheet’s original UTF-8, semicolon-delimited CSV export directly.

- Locate the required columns by their exact headers: `Weight` and `Date`.
- Do not rely on their positions; ignore every other column, including unnamed columns.
- Parse `Weight` as kilograms, converting the decimal comma to a decimal point before creating a `BigDecimal`.
- Parse `Date` as `dd/MM/yy`, explicitly interpreting two-digit years as 2000–2099.
- Trim surrounding whitespace.
- Skip records whose `Weight` cell is blank.
- Reject the file if either required header is absent.
- Keep the real CSV below the already ignored `data/` directory.
- Commit only reduced synthetic fixtures.

Use a proper semicolon-delimited CSV parser, such as Apache Commons CSV, so quoted values and embedded delimiters in unrelated columns cannot shift the `Weight` or `Date` fields. Add that dependency only to the importer module.

### Measured versus predicted values

Require an explicit last-measured date when running the importer. Skip and count
nonblank weights after that date so the original export can retain future
spreadsheet predictions without importing them as measurements. Reporting the
count keeps this exclusion visible.

The working assumption is that direct weights on or before this confirmed cutoff are measurements. If the spreadsheet contains matured predictions that were never replaced by measurements, they must be removed from the export or represented explicitly in a later import format.

### Existing data

Use the API’s current create-or-replace semantics:

- a new date creates a measurement;
- an existing date is replaced by the spreadsheet value;
- rerunning the same export produces the same stored values;
- blank or absent source rows do not delete existing database measurements.

This is an import, not ongoing spreadsheet synchronization.

## TRACKER-24 — Import data with a simple script

1. Create an appropriate TRACKER-8 task branch before changing code.

2. Add a small standalone Gradle `application` subproject, such as `importer/`.

- Use the repository’s Java toolchain.
- Keep it independent of the backend module and its domain classes.
- Use the JDK HTTP client and standard Java types. Add only a focused CSV-parsing dependency to the importer module.
- Wire its formatting and tests into `./gradlew format` and `./gradlew build`.

3. First add failing tests for the CSV reader and source mapping.

Cover:

- locating `Weight` and `Date` by header rather than position;
- ignoring the many unrelated columns and unnamed headers;
- parsing `95,1` as `BigDecimal("95.1")`;
- parsing `03/11/25` as `2025-11-03`;
- quoted unrelated fields containing semicolons;
- blank weights being skipped;
- missing required headers;
- malformed dates or weights;
- duplicate measurement dates;
- nonblank weights after the supplied last-measured date being skipped and counted;
- validation of the entire file before any HTTP request begins.

4. Implement the focused CSV reader and source model.

- Configure the reader for semicolon-delimited CSV with a header record.
- Permit unrelated blank headers present in the spreadsheet export.
- Extract only the `Weight` and `Date` values.
- Do not map the spreadsheet’s `Day`, rolling averages, or any other calculated columns.

5. Add a command-line entry point accepting explicit arguments such as:

```text
--input <csv-path>
--base-url <tracker-base-url>
--through <last-measured-date>
```

Invalid arguments or source data must exit nonzero before contacting Tracker.

## TRACKER-25 — Import data via the API

1. First add failing HTTP-adapter tests using a local synthetic HTTP server.

Cover:

- one POST per accepted measurement;
- the existing Journal path;
- JSON request mapping;
- `Content-Type: application/json`;
- successful `200` responses;
- connection failures, timeouts, and non-success responses;
- stopping with a nonzero result and identifying the failed row/date safely.

2. Implement the API client with the JDK HTTP client.

Send requests equivalent to:

```http
POST /api/journal/weight-measurement
Content-Type: application/json

{
  "date": "2025-01-01",
  "weightInKg": 82.10
}
```

3. Send rows sequentially.

- No concurrency is needed for roughly 300 measurements.
- Print counts for source rows, skipped blanks, successful imports, and failures.
- Do not print personal weight values.
- Document that an HTTP failure may leave a partial import.
- Rerunning is the recovery mechanism because the endpoint replaces by date.

4. Do not add a dedicated import endpoint.

The existing Journal endpoint already maps requests into `LogWeightMeasurementCommand`, applies Journal validation, and persists through `WeightMeasurementStore`. Reusing it avoids duplicating domain or persistence behavior.

## Documentation and operating procedure

Add a short importer README or workflow section describing:

1. Export the spreadsheet as its original semicolon-delimited CSV.
2. Confirm that the export contains the `Weight` and `Date` headers.
3. Keep the real export under ignored `data/`; no column removal or format normalization is required.
4. Starting Tracker against an empty or copied development database.
5. Running the importer against `http://127.0.0.1:8080`.
6. Checking representative first, middle, and last measurements through the API.
7. Running against `bootRunReal` only after explicit authorization to modify the personal database.
8. Retaining or creating a user-approved recoverable backup before the real import.

Do not mark the broader import milestone complete and do not change canonical domain documentation; the existing Journal and Import ownership decisions remain valid.

## Verification

During implementation:

1. Run focused importer tests after each TDD step.
2. Run a manual end-to-end import using a synthetic CSV and `:backend:bootRunEmpty`.
3. Verify representative measurements through the existing GET endpoint.
4. Run:

```bash
./gradlew format
./gradlew build
```

5. Inspect `git status` to ensure no CSV, database, generated output, or personal data is staged.
6. Commit TRACKER-24 and TRACKER-25 separately if the changes form clean independent increments.
7. Do not push.

## Acceptance criteria

- The original semicolon-delimited spreadsheet export can be imported without manually removing or rearranging columns.
- Every accepted row is sent through the existing Journal API.
- Blank weights create no measurement.
- Nonblank weights after the confirmed cutoff are skipped and reported.
- Predicted or calculated weights are not imported as measurements.
- Duplicate or malformed source rows fail before any HTTP request.
- API failures are visible and produce a nonzero exit status.
- Repeating a successful import does not create duplicate dates.
- No frontend, database schema, or backend production behavior changes.
- Personal spreadsheet values and databases remain outside Git and automated tests.
- `./gradlew build` passes.
