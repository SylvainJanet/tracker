# TRACKER-7 — Basic weight analysis implementation plan

## Goal

Add an Analysis user experience that displays measured weight and rolling-average trends over a selected inclusive calendar-date range.

The completed experience has this layout:

1. graph title;
2. interactive legend;
3. menu bar containing the displayed range and graph controls;
4. plot.

When the default analysis is globally empty, display:

> No weight measurement found

followed by:

> Nothing to display: no weight measurement was found.

The feature must preserve the distinction between measured weights, calculated rolling averages, and missing measurements.

> This is an active ticket plan. It may repeat implemented behaviour to establish
> the baseline for later tickets. Canonical decisions remain in the domain and
> architecture guides. Delete this plan when TRACKER-7 is complete.

## Current implementation baseline

- Backend Journal owns weight measurements and supports logging, lookup by date,
  inclusive range retrieval and retrieval of the first measurement date.
- Backend Analysis obtains Journal data through application contracts and maps
  it into Analysis-owned dated values and series.
- Backend Analysis derives stable timeline day numbers while retaining calendar
  dates and the represented date range.
- The Statistics supporting module exposes an indexed rolling-average
  application contract and is wired into Analysis.
- Statistics deliberately returns no rolling-average results yet; calculation
  behaviour belongs to TRACKER-15.
- `GET /api/analysis/weight` publishes the timeline, range, measurements and an
  empty `rollingAverages` collection.
- Frontend Journal owns its weight concepts and validation.
- Frontend Analysis validates and presents the backend-published result without
  sharing Journal’s weight domain or reproducing statistical calculations.
- The initial values and globally empty Analysis experiences are implemented.
- No charting dependency is installed yet.

No existing Flyway migration needs modification for this feature.

## Product interpretation

### Time and missing data

- Calendar dates are the source of truth.
- Day 1 is the calendar date of the first recorded weight.
- Later day numbers are calendar-day offsets from that date, not ordinal positions in the returned array.
- A missing day produces no measured-weight point.
- Missing weights are never represented as zero or as fabricated measurements.
- The line may connect the measurements on either side of a missing day, while their horizontal distance continues to represent the elapsed calendar time.
- Date ranges are inclusive.
- The default range begins on the first recorded weight date and ends on today.

This interpretation prevents a missing day from collapsing time and keeps calendar-based rolling windows meaningful.

### Context ownership

- Journal owns measured weights, their validation and their retrieval.
- Statistics owns reusable indexed numerical operations. It has no knowledge of
  weights, calendar dates, journaling rules or presentation.
- Analysis obtains context-owned data, translates it into Statistics inputs and
  translates mathematical outputs into meaningful dated analysis results.
- Strategy will own policies for resolving gaps or determining eligible data
  when those decisions are introduced; neither Analysis nor Statistics should
  infer them from Journal data.
- The frontend Analysis context validates and presents backend-published
  analysis results. It does not share frontend Journal’s domain, implement
  Strategy or Statistics, or recalculate backend results.
- ECharts types and configuration remain inside the frontend web adapter. They
  must not leak into frontend domain, application ports, services or backend
  contracts.

## Charting decision

Use:

- `echarts` 6.1.x, Apache-2.0;
- `ngx-echarts` 22.x, MIT and explicitly compatible with Angular 22.

Use ECharts’ modular imports so each lazy chart build includes only the capabilities that are registered and actually used.

ECharts provides the required behaviors:

- multiple line series;
- numeric or time axes;
- object-array datasets with named dimensions;
- automatic and explicitly controlled axis ranges;
- point symbols;
- hover tooltips;
- click events containing series and point data;
- series visibility changes;
- responsive resizing;
- optional future data zoom;
- generated ARIA descriptions.

Defer the choice between ECharts’ built-in title and a semantic Angular heading
until TRACKER-18. Likewise, defer the choice between ECharts’ built-in legend
and a semantic Angular HTML legend until TRACKER-20. These decisions should
consider placement, accessibility, testing, and customization.

The application, rather than ECharts, owns series labels, descriptions, visibility, and colors. Visual values should originate from the existing CSS token system rather than hard-coded TypeScript color literals.

### Integration boundary

ECharts is a presentation dependency of the frontend Analysis context. ECharts
types, options and events must remain inside its web adapter and must not leak
into presenter views, intents, application code or the backend contract.

### Registration and loading

Use the tree-shakable API from `echarts/core`.

Create an Analysis-specific ECharts setup module and load it dynamically through
`provideEchartsCore`. Keep the provider at the lazy Analysis/chart boundary
rather than registering ECharts in the application bootstrap.

Registration and code loading are different concerns:

- `echarts.use(...)` registers a capability on the shared ECharts module instance;
- the import graph and lazy route boundaries determine when its implementation is
  downloaded;
- registrations accumulate after their lazy chunk has loaded, but the code is
  loaded only once and is not copied into every component.

Register only capabilities that have an implemented use case, adding them with
the relevant ticket:

- TRACKER-14: `LineChart`, `GridComponent`, `CanvasRenderer`, and
  `AriaComponent`;
- TRACKER-16: `TooltipComponent`;
- TRACKER-20: `LegendComponent` only if the built-in ECharts legend is selected;
- `DatasetComponent` only if the chart adapter uses `dataset.source`.

Register `TitleComponent` or any other capability only if its ECharts
implementation is actually used.

Do not create a separate ECharts build for every chart component unless bundle
measurements demonstrate a material benefit. Charts displayed together would
load all those chunks anyway. A future independent lazy feature may have its own
setup module for additional chart types or components.

The official ECharts tree-shaking approach is documented in the
[Apache ECharts import guide](https://echarts.apache.org/handbook/en/basics/import/).

### Adapter and type boundaries

Define a local `ComposeOption` type containing exactly the registered and used
series and component option types.

Keep the conversion boundaries explicit:

1. the presenter exposes an ECharts-independent view;
2. a pure option mapper converts that view into ECharts options;
3. a chart event adapter converts ECharts event payloads into application-owned
   intents such as selecting a measurement date.

The HTTP contract remains independent of the chart library. It transports
calendar dates, backend-derived stable day numbers and weight values. The
frontend preserves those day numbers and derives only chart-specific option
objects.

### Testing strategy

Do not unit-test ECharts' Canvas renderer, generated pixels, path geometry,
tooltip positioning or hit testing. Those behaviours belong to ECharts.

Add automated tests for application-owned behaviour:

- presenter tests for loading, empty and populated views and user intents;
- HTTP adapter tests for contract mapping;
- pure option-mapper tests for series data, missing-date gaps, visibility,
  dynamic axes and other configuration decisions;
- event-adapter tests proving that valid ECharts node events become the expected
  application intent and unrelated events are ignored;
- shallow component tests proving that the chart view is passed to the chart
  boundary and normalized intents are forwarded;
- semantic component tests for the empty message, menus, dialogs, and any title
  or legend implementation rendered as Angular DOM.

Prefer focused assertions over snapshots of the complete ECharts option object,
because defaults and unrelated configuration should not make tests brittle.

Run the normal production build for every ticket. Whenever a ticket adds an
ECharts registration, also perform a browser smoke test confirming that:

- the chart initializes without missing-component warnings;
- the newly added capability works;
- resizing still works;
- the browser console remains clean.

A missing runtime registration is not necessarily caught by TypeScript or a
shallow test. Do not introduce a browser-test framework solely for this task,
but cover this smoke path in automated browser tests if the project later gains
that infrastructure.

## HTTP contract

### Resource

```http
GET /api/analysis/weight
GET /api/analysis/weight?startDate=2026-01-01&endDate=2026-09-24
```

TRACKER-13 initially uses the request without parameters. TRACKER-22 activates explicit ranges.

When range parameters are supported:

- either both parameters or neither must be supplied;
- both use ISO `YYYY-MM-DD` calendar dates;
- `startDate` must not be after `endDate`;
- invalid requests return Problem Details with status 400.

### Successful response

```json
{
  "timelineStartDate": "2026-01-02",
  "range": {
    "startDate": "2026-01-02",
    "endDate": "2026-09-24"
  },
  "weightMeasurements": [
    {
      "date": "2026-01-02",
      "dayNumber": 1,
      "weightInKg": 82.15
    },
    {
      "date": "2026-01-04",
      "dayNumber": 3,
      "weightInKg": 81.95
    }
  ],
  "rollingAverages": [
    {
      "windowInDays": 7,
      "points": [
        {
          "date": "2026-01-08",
          "dayNumber": 7,
          "averageWeightInKg": 81.983333,
          "includedMeasurementCount": 6,
          "completeCalendarWindow": true
        }
      ]
    }
  ]
}
```

Contract rules:

- `timelineStartDate` is the first measurement date and remains stable when the selected range changes.
- Every measured or calculated point includes the stable `dayNumber` derived by
  backend Analysis from its date and `timelineStartDate`.
- `range` is the range actually represented by the response.
- Measurements and rolling points are ordered by ascending date.
- No null placeholder is emitted for a missing measurement or unpublished rolling result.
- Units remain explicit in field names.
- `weightMeasurements` contains observations only.
- `rollingAverages` contains calculated results only.
- Series names, descriptions, colors, and visibility are frontend concerns.
- TRACKER-13 returns `rollingAverages: []`; TRACKER-15 extends the contract with
  calculated series.

### Globally empty response

Absence is a successful collection result, not a 404:

```json
{
  "timelineStartDate": null,
  "range": null,
  "weightMeasurements": [],
  "rollingAverages": []
}
```

This response drives the dedicated globally empty title and message defined in
the goal.

Once explicit ranges exist, a selected range without points must remain distinguishable from globally having no weight:

```json
{
  "timelineStartDate": "2026-01-02",
  "range": {
    "startDate": "2026-05-01",
    "endDate": "2026-05-10"
  },
  "weightMeasurements": [],
  "rollingAverages": []
}
```

The wording for this later selected-range empty state should be decided in TRACKER-22; it must not incorrectly say that no weight has ever been logged.

## Analysis point representation

Backend Analysis derives and publishes the stable day number for every measured
or calculated point:

```text
dayNumber = calendar days between timelineStartDate and point.date + 1
```

The calculation uses calendar-date arithmetic and is independent of time zones,
array positions and selected ranges. The frontend preserves the published day
number as backend-owned data. Its Analysis domain independently derives the
expected number only to reject a structurally incoherent result; it neither
replaces the published value nor becomes authoritative for the analysis.

Use calendar-date arithmetic without converting dates through instants, local
times or time zones, so daylight-saving changes cannot alter the result.

Conceptually, the measured dataset becomes:

```typescript
interface MeasuredWeightChartPoint {
  readonly date: CalendarDate;
  readonly dayNumber: number;
  readonly weightInKg: number;
}
```

A rolling dataset becomes:

```typescript
interface RollingAverageChartPoint {
  readonly date: CalendarDate;
  readonly dayNumber: number;
  readonly averageWeightInKg: number;
  readonly includedMeasurementCount: number;
  readonly completeCalendarWindow: boolean;
}
```

ECharts can consume these object arrays directly through `dataset.source` and `series.encode`, for example:

```text
x = dayNumber
y = weightInKg or averageWeightInKg
tooltip/detail identity = date
```

The frontend maps validated results for presentation. Its coherence check may
compare a published day number with the point’s calendar position, but it does
not calculate rolling averages, replace backend values or reconstruct missing
observations.

## Shared presentation state

Maintain one frontend series registry containing:

- stable series key;
- display label;
- short description;
- CSS color token;
- visible/hidden state.

Examples:

- `measured-weight`;
- `rolling-average-7-days`;
- `rolling-average-14-days`.

Both the legend and TRACKER-23 Graphs dialog dispatch visibility changes to the same presenter state. The ECharts options are derived from that state.

The date-indexed presentation model should also allow TRACKER-17 to find the measured weight and every published rolling value for a clicked date without another backend call.

## Rolling-average decision gate

Before TRACKER-15, explicitly decide:

1. Which windows the weight-analysis workflow requests. Historical evidence
   identifies 7, 14, 28, 60, 180 and 360 calendar days, but the accepted set
   remains a product decision.
2. On which timeline indexes Analysis publishes results: every represented day,
   only measurement dates, or another explicitly defined set.
3. Which input values are eligible and how gaps are resolved. This is a
   Strategy-owned decision. Missing values must remain observable to Strategy
   and must never be silently converted to zero by Analysis or Statistics.
4. Whether partial initial windows are published and how their coverage is
   represented.
5. The mathematical precision and rounding required from Statistics, separately
   from frontend display formatting.
6. How explicitly selected future ranges behave. The default range ends on
   today, so later measurements are outside that range without requiring a
   separate past, present or future classification.

A selected range beginning after the timeline origin must not change the
calculated value at its first date. Analysis must obtain enough context-owned
pre-range input for the largest requested window and translate it into indexed
Statistics input. Statistics performs the numerical calculation without knowing
the dates or their business meaning. Analysis then restores the dates and clips
the published points to the requested display range.

## Ticket plan

### TRACKER-13 — Display weight values or “no weight logged yet”

Backend:

- [x] Add sorted inclusive range retrieval and first-measurement-date retrieval
      to Journal’s application contracts and store.
- [x] Keep `Weight` and `WeightMeasurement` owned by Journal.
- [x] Add Analysis-owned `DatedValue` and `DatedSeries` representations.
- [x] Add the parameterless `GetWeightAnalysis` use case.
- [x] Inject the current date through an explicit clock.
- [x] Have Analysis obtain measurements through Journal inbound use cases rather
      than accessing Journal persistence or domain objects.
- [x] Create the Statistics supporting module and its rolling-average application
      contract.
- [x] Wire Analysis to Statistics while keeping the Statistics result empty
      until TRACKER-15.
- [x] Expose `GET /api/analysis/weight`.
- [x] Return 200 with the globally empty representation when no eligible
      measurement exists.
- [x] Derive stable timeline day numbers in Analysis and expose them through the
      application and HTTP contracts.
- [x] Update the OpenAPI contract test.

Frontend:

- [x] Introduce the Analysis context and Weight navigation destination.
- [x] Add HTTP response validation and translation.
- [x] Keep frontend Journal weight concepts separate from frontend Analysis.
- [x] Validate the published timeline, range, measurement dates, day numbers and
      positive finite values as one coherent Analysis result.
- [x] Add explicit loading, data, empty and failure presentation states.
- [x] Render returned dates and kilogram values as semantic text.
- [x] Render the dedicated empty title and message only for the globally empty
      result.
- [x] Add production-route composition coverage.
- [x] Preserve backend-provided day numbers through the application and
      presentation boundaries.

Tests:

- [x] Repository integration tests for ordering, inclusive bounds and missing
      dates.
- [x] Backend domain tests for dated-series invariants.
- [x] Backend application tests for the default range, future-only histories,
      empty results and Statistics collaboration.
- [x] MVC tests for populated and empty responses.
- [x] Frontend gateway tests for valid, empty, malformed and failed responses.
- [x] Frontend domain tests for structural and semantic result coherence.
- [x] Presenter and component tests for loading, values, empty message and
      failure.

### TRACKER-14 — Display weight graph

- Install the pinned ECharts and Angular wrapper dependencies and update the lockfile.
- Register only required ECharts modules.
- Create a chart-specific adapter that converts the presenter’s view to ECharts options.
- Plot `dayNumber` against `weightInKg`.
- Keep point symbols visible.
- Preserve horizontal gaps between non-consecutive dates.
- Make the chart responsive.
- Provide a concise ARIA description and a non-chart fallback for the underlying values.
- Test the chart mapper and page bindings without testing ECharts internals.

### TRACKER-15 — Display rolling averages

- Resolve the rolling-average decision gate before implementing calculation
  behaviour.
- Define any eligibility or missing-data policy in Strategy rather than
  embedding it in Analysis or Statistics.
- Extend Statistics with generic indexed-series and rolling-window domain
  concepts only where their mathematical invariants require them.
- Implement rolling-average calculations in Statistics without dates, weights,
  units, Journal rules or presentation concepts.
- Have Analysis obtain Journal data and any applicable Strategy decision through
  their application contracts.
- Have Analysis translate the selected data and windows into indexed Statistics
  input.
- Keep results independent of the selected display range by obtaining the
  required lookback before requesting the calculation.
- Translate Statistics indexes and numerical results into Analysis-owned dated
  rolling-weight results.
- Include the accepted coverage metadata in every published point.
- Extend the HTTP response through `rollingAverages`.
- Extend frontend Analysis validation to accept coherent rolling results without
  reproducing the calculation.
- Add one frontend line series per window.
- Test Statistics calculations independently for:
  - complete and partial windows;
  - sparse indexes;
  - exact inclusive boundaries;
  - empty input;
  - precision and rounding;
  - output ordering.
- Test Analysis orchestration independently for:
  - Journal and Strategy collaboration;
  - range lookback;
  - index-to-date translation;
  - coverage metadata;
  - response ordering and empty results.
- Test frontend validation and presentation of valid and incoherent rolling
  results.

No calculated value should be persisted during this task unless a separate
persistence decision is made.

### TRACKER-16 — Display weight when hovering a graph node

- Enable item tooltips.
- Display series label, calendar date, and formatted kilogram value.
- Display the rolling window and coverage when hovering a rolling-average point.
- Keep formatting in the frontend.
- Test the tooltip formatter as a pure function.

### TRACKER-17 — Display details when clicking a graph node

- Forward ECharts point-click events from the page.
- Ignore clicks without a concrete series datum.
- Use the datum’s calendar date as the dialog identity.
- Open an accessible dialog showing:
  - date;
  - measured weight, or an explicit missing indication;
  - every published rolling-average value for that date;
  - coverage/partial status where applicable.
- Do not perform a second HTTP request when the loaded response already contains the information.
- Test click forwarding, date lookup, dialog content, and close behavior.

### TRACKER-18 — Display graph title, axis labels, units and values

- Decide whether to use ECharts’ built-in title or a semantic Angular heading,
  considering placement, accessibility, testing, and customization.
- Render the selected title implementation above the legend, menu bar, and plot.
- Label the x-axis as elapsed day number.
- Label the y-axis as weight in kilograms.
- Format values without changing calculation precision.
- Ensure title and axis names are also represented in the accessible description.

This work is technically small and may be developed with TRACKER-14 while remaining a separately verifiable ticket.

### TRACKER-19 — Dynamic graph range and axis labels

Implement pure, unit-tested axis policies.

X-axis:

- minimum and maximum correspond to the selected dates’ day numbers;
- short ranges may label every day;
- medium ranges use approximately 7- or 14-day intervals;
- longer ranges choose a readable multiple based on span and available width;
- labels remain day numbers even though point identity remains a date.

Y-axis:

- determine the extent from currently visible series;
- add padding so equal or nearly equal values remain readable;
- recommended initial intervals:
  - span below 0.2 kg: 0.05 kg;
  - span below 2 kg: 0.1 kg;
  - span below 5 kg: 0.5 kg;
  - span below 20 kg: 1 kg;
  - larger span: a “nice” 1/2/5 × power-of-ten step targeting roughly 6–10 labels;
- round axis bounds outward to the selected interval.

Recalculate the y-axis when series visibility changes.

### TRACKER-20 — Display plot legend with toggle on click

- Decide whether to use ECharts’ built-in legend or a semantic Angular HTML legend, considering placement, accessibility,
  testability, and synchronization with the Graphs dialog.
- Render the selected legend implementation between the title and menu bar.
- Give every item its series color and name.
- Keep the application’s shared series registry as the source of truth for visibility.
- Make every legend item operable and expose its visible or hidden state accessibly.
- Grey out hidden series while retaining readable contrast.
- Rebuild chart options and dynamic y-axis bounds after a visibility change.
- Test application-owned visibility behavior and event mapping without testing ECharts’ rendering internals.

### TRACKER-21 — Display menu bar with dates

- Add the menu bar below the legend and above the plot.
- Display:
  - `Range: DD/MM/YYYY - DD/MM/YYYY`;
  - a `Graphs` button reserved for TRACKER-23.
- In this ticket the range is informational.
- Format dates for display while retaining ISO dates internally.
- Hide or disable range actions appropriately in the globally empty state.

### TRACKER-22 — Allow choosing the date range

- Make both displayed dates operable date controls.
- Constrain and validate the selected range according to the accepted product decision.
- Apply only when both dates form a valid inclusive range.
- Send ISO query parameters to the analysis endpoint.
- Preserve the previously displayed result during loading where helpful.
- Prevent an older response from replacing a newer selection.
- Consider storing the selected range in route query parameters so navigation preserves it.
- Keep `timelineStartDate` stable so day numbers do not reset when the range changes.
- Add tests for valid changes, reversed dates, boundaries, empty selected ranges, backend failures, and rapid consecutive requests.

### TRACKER-23 — Graph descriptions and visibility dialog

- Open an accessible dialog from the `Graphs` button.
- List every available series with:
  - label;
  - short description;
  - visible/hidden toggle.
- Reuse the same visibility state as the legend.
- Ensure changes are immediately reflected in both places and in the plot.
- Keep descriptions in frontend-owned configuration.
- Test dialog opening, descriptions, toggle synchronization, and closing.

## Recommended ticket grouping

Keep the ticket order, with these implementation efficiencies:

- TRACKER-14 and TRACKER-18 can share the initial chart configuration.
- TRACKER-16 and TRACKER-17 share stable point identity and event handling.
- TRACKER-20 should establish the visibility model later reused by TRACKER-23.
- TRACKER-21 should establish the range view model later made interactive by TRACKER-22.

Each ticket should still have its own observable acceptance boundary and focused commit.

## Verification and delivery

For every ticket:

1. add the lowest-level failing test first;
2. add integration or architecture coverage when a boundary changes;
3. implement the smallest passing vertical slice;
4. run `./gradlew format`;
5. run `./gradlew build`;
6. commit with a focused TRACKER ticket message.

Do not inspect or use personal data. Automated tests must use synthetic measurements and isolated SQLite databases.

## External references

- [Apache ECharts features](https://echarts.apache.org/en/feature.html)
- [Apache ECharts datasets](https://echarts.apache.org/handbook/en/concepts/dataset/)
- [Apache ECharts axes](https://echarts.apache.org/handbook/en/concepts/axis/)
- [Apache ECharts events and actions](https://echarts.apache.org/handbook/en/concepts/event/)
- [Apache ECharts accessibility](https://echarts.apache.org/handbook/en/best-practices/aria/)
- [ngx-echarts Angular wrapper](https://github.com/xieziyu/ngx-echarts)
- [Apache ECharts license and releases](https://github.com/apache/echarts)
