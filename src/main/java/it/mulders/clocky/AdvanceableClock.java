package it.mulders.clocky;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A clock that by itself will not progress other than when it is explicitly told to do so.
 * This clock satisfies the assumption about clocks that time is strictly increasing.
 */
public final class AdvanceableClock extends Clock {
    private Instant instant;
    private final ZoneId zoneId;

    /**
     * Creates an instance that initially returns the provided initial instant. This clock runs in the system's default
     * time zone.
     * @param initialInstant the initial time of this clock.
     */
    public AdvanceableClock(final Instant initialInstant) {
        this(initialInstant, ZoneId.systemDefault());
    }

    /**
     * Creates an instance that initially returns the provided initial instant.
     * @param initialInstant the initial time of this clock.
     * @param zoneId the time zone to use to convert the instant to date-time.
     */
    public AdvanceableClock(final Instant initialInstant, final ZoneId zoneId) {
        Objects.requireNonNull(initialInstant, "Initial instant may not be null");
        Objects.requireNonNull(zoneId, "Zone ID may not be null");

        this.instant = initialInstant;
        this.zoneId = zoneId;
    }

    /**
     * Manually advances the time of this clock by the specified duration. The duration may not be negative.
     * @param duration amount of time to advance this clock by.
     */
    public synchronized void advanceBy(final Duration duration) {
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration may not be negative");
        }

        instant = instant.plus(duration);
    }

    /** {@inheritDoc} */
    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    /** {@inheritDoc} */
    @Override
    public Clock withZone(ZoneId zoneId) {
        if (Objects.equals(this.zoneId, zoneId)) {
            return this;
        }

        return new AdvanceableClock(instant, zoneId);
    }

    /** {@inheritDoc} */
    @Override
    public Instant instant() {
        return instant;
    }

    @Override
    public final boolean equals(Object other) {
        if (other instanceof AdvanceableClock) {
            final AdvanceableClock that = (AdvanceableClock) other;
            return this.instant.equals(that.instant) && this.zoneId.equals(that.zoneId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, zoneId);
    }
}
