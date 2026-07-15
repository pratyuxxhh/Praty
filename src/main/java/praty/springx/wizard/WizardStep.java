package praty.springx.wizard;

import praty.springx.terminal.KeyPress;

/**
 * A single interactive wizard step producing a value of type {@code T}.
 */
public interface WizardStep<T> {

    String title();

    /**
     * Renders the current step frame to the terminal.
     */
    void render();

    /**
     * Handles a key press. Returns true if the step should advance.
     */
    boolean handle(KeyPress key);

    boolean isComplete();

    T value();

    /**
     * Whether the user can go back from this step.
     */
    default boolean canGoBack() {
        return true;
    }
}
