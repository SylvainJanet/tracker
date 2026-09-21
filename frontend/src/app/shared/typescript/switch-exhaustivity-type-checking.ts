/**
 * Can be used as a bottom type at the end of switch statements
 * to provide for compile time exhaustiveness checking
 *
 * Usage:
 * switch (this.value) {
 *       case 'first-case':
 *         return "something";
 *       case 'second-case':
 *         return "something else;
 *       case 'final-case':
 *         return "foo bar";
 *       default:
 *         return bottom(this.value);
 *     }
 *
 * @param value The bottom value
 * @param onNever If the bottom is actually executed an action response that can optionally return a default value
 */
export function exhaustiveSwitchCheck<T = never>(
  value: never,
  onNever?: (value: never) => T,
): T | never {
  if (!onNever) {
    throw new Error(`bottom hit: ${JSON.stringify(value)}`);
  } else {
    return onNever(value);
  }
}
