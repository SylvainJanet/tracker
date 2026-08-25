# TrackerWeb

This project was generated using [Angular CLI](https://github.com/angular/angular-cli)
version 22.1.5.

## Setup

Use the Node.js version pinned in `.nvmrc`. This requirement also applies when
frontend commands are launched through Gradle. Then install the locked dependencies:

```bash
npm ci
```

## Development server

To start a local development server, run:

```bash
npm start
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`.
The application will automatically reload whenever you modify any of the source files.

## Debugging

Frontend application code runs in the browser. The Angular development build
includes source maps, so `npm start` is also the correct command when debugging.

When running the complete application from the repository root, use:

```bash
./run.sh --mode=debug
```

Then open a browser JavaScript debugger at http://localhost:4200. The backend
waits for a remote JVM debugger on localhost:5005.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
npm exec -- ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`,
or `pipes`), run:

```bash
npm exec -- ng generate --help
```

## Building

To build the project run:

```bash
npm run build
```

This will compile your project and store the build artifacts in the `dist/` directory.
By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the
following command:

```bash
npm test -- --watch=false
```

## Linting and formatting

Run the configured frontend checks with:

```bash
npm run lint
npm run format:check
```

Apply formatting with `npm run format`.

## End-to-end tests

No end-to-end runner is currently configured. Add one only when a valuable complete
workflow justifies it, as described in the project testing guide.

## Additional Resources

For more information on using the Angular CLI, including detailed command references,
visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli)
page.
