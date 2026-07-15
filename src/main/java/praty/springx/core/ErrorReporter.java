package praty.springx.core;

import java.io.PrintStream;

/**
 * Prints {@link SpringxException} messages without stack traces.
 */
public final class ErrorReporter {

    private final PrintStream out;
    private final PrintStream err;

    public ErrorReporter() {
        this(System.out, System.err);
    }

    public ErrorReporter(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public void report(SpringxException exception) {
        err.println(exception.formatForTerminal());
    }

    public void report(Result<?> result) {
        result.error().ifPresent(this::report);
    }

    public void reportUnexpected(Throwable throwable) {
        String message = throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
        SpringxException wrapped = new SpringxException(
                "Something went wrong.",
                message,
                "Run praty spring doctor for environment checks."
        );
        report(wrapped);
    }

    public PrintStream out() {
        return out;
    }

    public PrintStream err() {
        return err;
    }
}
