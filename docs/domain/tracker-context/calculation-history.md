# Calculation history

## Purpose

Calculation definitions changed during the tracker redesign. The redesign
therefore distinguished calculation-method history from the histories of
observations, goals and policies.

Analysis owns calculation-method history and the derived results produced by
those methods. Journal owns observations and predictions, while Strategy owns
goals and policies.

It also retained method identities, resolved configuration and useful
intermediate calculations so that historical calculated values could be traced
to the rules and methodology that produced them.

Correcting an implementation defect was not automatically treated as adopting a
new calculation method.

## Historical spreadsheet decomposition

The redesign introduced a three-layer calculation structure:

- `09a Calc resolution` selected the facts, goals, rules and method for a date;
- `09b Calc subcalc` performed calculations and exposed useful intermediate
  values;
- `09c Calc results` referenced values intended for presentation or other uses.

This decomposition made large formulas easier to inspect and avoided repeating
calculation logic in the intended results layer. It is historical evidence, not
required application architecture.

Resolution recorded identifiers as well as copied numerical values. A number can
show what was used, but an identifier can also explain which historical goal,
boundary rule, maintenance assumption or method selected it. Retaining both
identifiers and resolved values allowed calculations to be traced to the
historical goal, rule, maintenance assumption or method that supplied them.

Inputs included observations, predictions, goals and configured parameters.
Calculated values included interpolated weights, thresholds, rolling balances
and activity estimates. A derived value may feed another calculation, but that
does not turn it into an original observation.

## Methods, goals and effective dates

A calculation method identifies how a family of derived values is produced.
Calculation-method history was maintained separately from goal and policy
history.

A goal changes when the intended target or strategy changes—for example, calorie
target, expected weight endpoint or protein ratio. A calculation method changes
when the same goal or policy is transformed differently. For example, the
boundary policy can remain unchanged while the method used to derive boundaries
for other analysis windows changes. The two decisions are therefore distinct.
See [Goals and day types](goals-and-day-types.md).

To process a date:

- the applicable method identity was associated with the date for provenance;
- when calculation logic changed, the spreadsheet formulas themselves changed
  prospectively from the effective date;
- the method ID documented which methodology those rows represented rather than
  dynamically dispatching the formula.

An adopted materially different calculation method received a distinct method
identity rather than being presented as equivalent historical behaviour.
Detailed boundary examples belong to [Analysis and boundaries](analysis-and-boundaries.md).

## Implementation corrections

Formula defects were not automatically treated as new calculation methods.
Correcting an implementation mistake and deliberately adopting a different
calculation methodology were distinct kinds of change.

## Method definitions and parameters

Historised calculation-method definitions could retain method-specific
parameters with explicit names, scopes and values. The provisional boundary
method, for example, stored a variation buffer for each analysis window.

Method parameters therefore belonged to the methodology they described rather
than to goals or observations. Not every configurable calculation value was a
method parameter: some rules, including protein and fibre rounding increments,
were part of goal history because they described the adopted nutritional
strategy.

Calculation rules and display formatting remained distinct. Formatting a value
to a chosen number of decimal places did not by itself define a different
historical calculation method.

Handling of missing, incomplete and intentionally unmeasured observations is
documented with the calculations to which those values contribute; see
[Analysis and boundaries](analysis-and-boundaries.md).

## Traceability and intermediate calculations

The redesign deliberately retained applicable identifiers, resolved values and
useful intermediate calculations so that complex results could be inspected and
traced back to the configuration and methodology that produced them.

Intermediate calculations remained derived values. Their use as helpers for
later calculations did not turn them into observations, goals or independently
adopted rules.

## Workbook evidence and testing

Spreadsheet formulas document previously implemented behaviour and edge cases.
They can be used as evidence when reproducing or checking historical calculation
semantics in the application. Cell coordinates, fill ranges and helper-column
layout are spreadsheet mechanics rather than domain rules, and formulas must not
be copied mechanically into application code.

## Durable distinctions

- Observations, goals, policies, calculation methods and derived values have
  different meanings and histories.
- Calculation-method history is separate from goal and policy history.
- Method identities provide provenance; spreadsheet formulas did not dynamically
  dispatch through those identities.
- Adopted calculation-method changes applied prospectively rather than silently
  rewriting earlier calculation behaviour.
- Correcting an implementation defect is not automatically a new calculation
  method.
- Method-specific parameters retain their meanings and scopes.
- Intermediate calculations remain derived values rather than source
  observations.
- Spreadsheet formulas and helper structures are evidence of historical
  behaviour, not application architecture.
