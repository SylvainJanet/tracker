package fr.sylvainjanet.tracker.importer.cli;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;

public record ImporterArguments(Path input, URI baseUrl, LocalDate through) {}
