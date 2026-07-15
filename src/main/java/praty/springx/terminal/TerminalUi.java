package praty.springx.terminal;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * High-level terminal interactions. Backed by JLine in Phase 3.
 */
public interface TerminalUi {

    String textInput(String label, String defaultValue, String placeholder);

    <T> Optional<T> select(String label, List<T> options, java.util.function.Function<T, String> display);

    <T> List<T> multiSelect(String label, List<T> options, java.util.function.Function<T, String> display);

    boolean confirm(String label, boolean defaultValue);

    void info(String message);

    void success(String message);

    void warn(String message);

    void error(String message);

    ProgressHandle progress(String label);

    interface ProgressHandle extends AutoCloseable {
        void update(double fraction, String status);

        void complete(String message);

        @Override
        void close();
    }

    void withSpinner(String label, Runnable work);

    void withSpinner(String label, Consumer<SpinnerControl> work);

    interface SpinnerControl {
        void update(String status);
    }
}
