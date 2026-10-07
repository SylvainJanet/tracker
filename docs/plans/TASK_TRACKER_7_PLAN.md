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

The implemented baseline supports the measured-weight line plus one rolling-average
line for each accepted window. Series meaning, labels, colours, coverage intensity
and accessible descriptions remain application-owned. The graph is responsive,
and the Analysis page retains a semantic table as a non-chart representation of
the measured weights. Later tickets add interactions, visibility controls and
further descriptions without exposing ECharts types outside the adapter.

Exact dependency versions, module registration, loading configuration and adapter
structure are owned by executable configuration and production code.

## Future range selection

When explicit range selection is introduced, requests must provide both inclusive
calendar-date boundaries or neither. Reversed or incomplete ranges are invalid.

A represented range containing no points must remain distinguishable from global
absence. The stable timeline origin and requested range remain available while
the measurement and calculated-series collections are empty. Presentation for
this state must not claim that no weight has ever been logged.

## Rolling-average baseline

The implemented weight-analysis workflow:

- requests trailing windows of 7, 14, 28, 60, 180 and 360 calendar days;
- includes only directly recorded weight measurements;
- leaves missing dates absent rather than converting them to zero or filling them;
- considers every date in the represented range and publishes a point whenever
  its trailing window contains at least one measurement;
- publishes incomplete windows and includes every contributing measurement so
  coverage and provenance are immediately available to frontend workflows;
- publishes an exact fractional result together with half-up `PRETTY` and
  `PRECISE` approximations at two and twenty decimal places;
- exposes window series through `rollingAverageSeries`, ordered by ascending
  window size, with points and included measurements ordered by ascending date;
- validates result coherence in frontend Analysis without reproducing the
  statistical calculation;
- calculates results on demand without persisting them.

No Strategy eligibility or gap-resolution policy is required for this method
because every direct measurement in the calendar window is eligible. Other
analysis methods must obtain such policies from Strategy when their eligibility
rules require them.

When future range selection begins after the timeline origin, the calculated
value at its first date must remain unchanged. Analysis must obtain enough
pre-range input for the largest requested window, restore Statistics indexes to
dates and clip only the published points to the requested display range.

## Ticket plan

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
