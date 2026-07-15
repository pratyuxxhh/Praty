package praty.springx.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Discriminated success/failure result. Prefer this over throwing for expected failures.
 */
public final class Result<T> {

    private final T value;
    private final SpringxException error;

    private Result(T value, SpringxException error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(Objects.requireNonNull(value, "value"), null);
    }

    /**
     * Success with no payload (for {@code Result<Void>} operations).
     */
    @SuppressWarnings("unchecked")
    public static Result<Void> okVoid() {
        return (Result<Void>) (Result<?>) new Result<>(null, null);
    }

    public static <T> Result<T> fail(SpringxException error) {
        return new Result<>(null, Objects.requireNonNull(error, "error"));
    }

    public static <T> Result<T> fail(String message, String reason, String suggestion) {
        return fail(new SpringxException(message, reason, suggestion));
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isFail() {
        return error != null;
    }

    public T get() {
        if (error != null) {
            throw error;
        }
        return value;
    }

    public T orElse(T fallback) {
        return error == null ? value : fallback;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }

    public Optional<SpringxException> error() {
        return Optional.ofNullable(error);
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        if (error != null) {
            return fail(error);
        }
        return ok(mapper.apply(value));
    }

    public <U> Result<U> flatMap(Function<T, Result<U>> mapper) {
        if (error != null) {
            return fail(error);
        }
        return mapper.apply(value);
    }
}
