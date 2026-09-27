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

Use Apache ECharts for the interactive weight graph. It remains a presentation
dependency and must not leak into domain models, application contracts or the
backend API.

Application-owned views and intents remain independent of the chart library.
Feature contexts provide library-independent graph inputs to a shared,
input-driven graph component in the frontend web adapter. The shared adapter
owns their conversion to ECharts options.

The implemented baseline supports one line series of numeric points. Series
meaning, labels, visibility, colours and accessible descriptions remain
application-owned. The graph is responsive, and the Analysis page retains a
semantic table as a non-chart representation of the same measurements. Later
tickets may extend the shared contract with multiple series and interactions
without exposing ECharts types outside the adapter.

Exact dependency versions, module registration, loading configuration and adapter
structure are owned by executable configuration and production code.

## Future range selection

When explicit range selection is introduced, requests must provide both inclusive
calendar-date boundaries or neither. Reversed or incomplete ranges are invalid.

A represented range containing no points must remain distinguishable from global
absence. The stable timeline origin and requested range remain available while
the measurement and calculated-series collections are empty. Presentation for
this state must not claim that no weight has ever been logged.

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
- Evolve the shared graph input from its current single-series contract to
  support the measured-weight series plus one line series per rolling window,
  without exposing ECharts types outside the shared web adapter.
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

TRACKER-14 deliberately left graph titles, axis labels, units and value
formatting to this separately verifiable ticket. Extend the shared graph input
with the chosen presentation metadata without exposing ECharts types outside
the shared web adapter.

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

## External references

- [Apache ECharts features](https://echarts.apache.org/en/feature.html)
- [Apache ECharts datasets](https://echarts.apache.org/handbook/en/concepts/dataset/)
- [Apache ECharts axes](https://echarts.apache.org/handbook/en/concepts/axis/)
- [Apache ECharts events and actions](https://echarts.apache.org/handbook/en/concepts/event/)
- [Apache ECharts accessibility](https://echarts.apache.org/handbook/en/best-practices/aria/)
- [ngx-echarts Angular wrapper](https://github.com/xieziyu/ngx-echarts)
- [Apache ECharts license and releases](https://github.com/apache/echarts)
