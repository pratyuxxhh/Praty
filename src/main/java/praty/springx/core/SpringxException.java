package praty.springx.core;

/**
 * User-facing springx error. Never dump stack traces for these.
 */
public final class SpringxException extends RuntimeException {

    private final String reason;
    private final String suggestion;

    public SpringxException(String message, String reason, String suggestion) {
        super(message);
        this.reason = reason == null ? "" : reason;
        this.suggestion = suggestion == null ? "" : suggestion;
    }

    public String reason() {
        return reason;
    }

    public String suggestion() {
        return suggestion;
    }

    /**
     * Formats a terminal-friendly multi-line message (no stack trace).
     */
    public String formatForTerminal() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());
        if (!reason.isBlank()) {
            sb.append("\nReason:\n").append(reason);
        }
        if (!suggestion.isBlank()) {
            sb.append("\nSuggestion:\n").append(suggestion);
        }
        return sb.toString();
    }
}
