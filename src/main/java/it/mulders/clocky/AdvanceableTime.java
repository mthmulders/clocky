package it.mulders.clocky;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Container to control time. Intended to be used in conjunction with {@link ManualClock}.
 * This class satisfies the assumption that time is strictly increasing.
 */
public final class AdvanceableTime {
    private Instant instant;

    /**
     * Creates an instance that initially returns the provided initial instant.
     * @param initialInstant the initial point in time.
     */
    public AdvanceableTime(final Instant initialInstant) {
        Objects.requireNonNull(initialInstant, "Initial instant may not be null");

        this.instant = initialInstant;
    }

    /**
     * @return the current time.
     */
    public Instant instant() {
        return instant;
    }

    /**
     * Manually advances the time by the specified duration. The duration may not be negative.
     * @param duration amount of time to advance by.
     */
    public synchronized void advanceBy(final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration may not be negative");
        }

        instant = instant.plus(duration);
    }

    @Override
    public final boolean equals(Object other) {
        if (other instanceof AdvanceableTime) {
            return this.instant.equals(((AdvanceableTime) other).instant);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return instant.hashCode();
    }
}
