package fr.sylvainjanet.tracker.importer.api;

public final class TrackerJournalClientException extends RuntimeException {

    public TrackerJournalClientException(String message) {
        super(message);
    }

    public TrackerJournalClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
