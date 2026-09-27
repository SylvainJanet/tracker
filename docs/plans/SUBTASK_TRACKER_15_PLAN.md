## TRACKER-15 implementation plan

### Goal

Display measured weight and rolling averages for 7, 14, 28, 60, 180, and 360 days on the existing weight-analysis graph.

### Accepted behavior

- The backend calculates and publishes the rolling averages for each day with a measurement.
- The backend is considered complete for now. Only the frontend needs work.

### 5. Extend frontend Analysis

- Validate and preserve the exact sum as a string.
- Validate positive finite approximations.
- Validate positive safe-integer counts not exceeding the window.
- Validate dates, day numbers, ordering, window uniqueness, and calendar completeness.
- Do not recalculate or compare the published approximation in the frontend.
- Carry rolling results through the gateway, domain, application service, presenter, and view models.

### 6. Support multiple connected graph series

Evolve the shared graph contract from one series to an ordered collection.

- Keep graph points numeric and non-null.
- Map each collection entry to an ECharts line series.
- Let ECharts connect every provided point.
- Add optional point intensity.
- Convert intensity to a non-zero opacity range in the graph adapter.
- Resolve CSS color tokens independently for every series.

Test multiple series, ordering, colors, point intensity, and connection across sparse x-values.

### 7. Present the rolling averages

Produce graph series in this order:

1. measured weight;
2. 7-day average;
3. 14-day average;
4. 28-day average;
5. 60-day average;
6. 180-day average;
7. 360-day average.

For rolling points:

- graph `y` uses `averageWeightInKgApproximation`;
- intensity uses `includedMeasurementCount / windowInDays`;
- each series receives a stable, distinct semantic color;
- no missing points are synthesized.

Update the accessible graph description and provide a semantic representation of rolling values and coverage without displaying the technical exact sum.

### 8. Document and verify

Update the canonical analysis documentation with the accepted calculation, coverage, precision, and missing-data behavior. Keep future Strategy/versioning details explicitly deferred.

Run:

```bash
./gradlew format
./gradlew build
```

Review the final diff for unrelated files, generated output, and personal data. Commit the work in focused backend calculation, backend publication, frontend presentation, and documentation commits.le
