package praty.springx.terminal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Scripted {@link TerminalUi} for non-interactive wizard tests.
 */
public final class ScriptedTerminalUi implements TerminalUi {

    private final Deque<String> textAnswers = new ArrayDeque<>();
    private final Deque<Integer> selectIndexes = new ArrayDeque<>();
    private final Deque<List<Integer>> multiSelectIndexes = new ArrayDeque<>();
    private final Deque<Boolean> confirmAnswers = new ArrayDeque<>();
    private final List<String> messages = new ArrayList<>();

    public ScriptedTerminalUi enqueueText(String... answers) {
        for (String answer : answers) {
            textAnswers.addLast(answer);
        }
        return this;
    }

    public ScriptedTerminalUi enqueueSelect(int... indexes) {
        for (int index : indexes) {
            selectIndexes.addLast(index);
        }
        return this;
    }

    public ScriptedTerminalUi enqueueMultiSelect(List<Integer> indexes) {
        multiSelectIndexes.addLast(List.copyOf(indexes));
        return this;
    }

    public ScriptedTerminalUi enqueueConfirm(boolean... answers) {
        for (boolean answer : answers) {
            confirmAnswers.addLast(answer);
        }
        return this;
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }

    @Override
    public String textInput(String label, String defaultValue, String placeholder) {
        if (textAnswers.isEmpty()) {
            return defaultValue == null ? "" : defaultValue;
        }
        return textAnswers.removeFirst();
    }

    @Override
    public <T> Optional<T> select(String label, List<T> options, Function<T, String> display) {
        int index = selectIndexes.isEmpty() ? 0 : selectIndexes.removeFirst();
        if (index < 0 || index >= options.size()) {
            return Optional.empty();
        }
        return Optional.of(options.get(index));
    }

    @Override
    public <T> List<T> multiSelect(String label, List<T> options, Function<T, String> display) {
        List<Integer> indexes = multiSelectIndexes.isEmpty() ? List.of() : multiSelectIndexes.removeFirst();
        List<T> selected = new ArrayList<>();
        for (Integer index : indexes) {
            if (index >= 0 && index < options.size()) {
                selected.add(options.get(index));
            }
        }
        return selected;
    }

    @Override
    public boolean confirm(String label, boolean defaultValue) {
        if (confirmAnswers.isEmpty()) {
            return defaultValue;
        }
        return confirmAnswers.removeFirst();
    }

    @Override
    public void info(String message) {
        messages.add("INFO:" + message);
    }

    @Override
    public void success(String message) {
        messages.add("OK:" + message);
    }

    @Override
    public void warn(String message) {
        messages.add("WARN:" + message);
    }

    @Override
    public void error(String message) {
        messages.add("ERR:" + message);
    }

    @Override
    public ProgressHandle progress(String label) {
        return new ProgressHandle() {
            @Override
            public void update(double fraction, String status) {
            }

            @Override
            public void complete(String message) {
                success(message);
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public void withSpinner(String label, Runnable work) {
        work.run();
    }

    @Override
    public void withSpinner(String label, Consumer<SpinnerControl> work) {
        work.accept(status -> {
        });
    }
}
