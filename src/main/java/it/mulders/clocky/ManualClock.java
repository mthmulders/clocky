/*
 * Copyright 2020 Maarten Mulders
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.mulders.clocky;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A clock that can by itself will not progress other than when it is explicitly told to do so.
 * This is a mixture between the {@link Clock.FixedClock} and the regular {@link Clock.SystemClock} implementations.
 */
public final class ManualClock extends Clock {
    private final Supplier<Instant> supplier;
    private final ZoneId zoneId;

    /**
     * Creates an instance that always returns the instant returned by a factory method. The clock runs in the systems
     * default time-zone.
     * @param instantSupplier the function to be invoked when asked for the current time.
     */
    public ManualClock(final Supplier<Instant> instantSupplier) {
        this(instantSupplier, ZoneId.systemDefault());
    }

    /**
     * Creates an instance that always returns the instant returned by a factory method.
     * @param instantSupplier the function to be invoked when asked for the current time.
     * @param zoneId the time-zone to use to convert the instant to date-time.
     */
    public ManualClock(final Supplier<Instant> instantSupplier, final ZoneId zoneId) {
        this.supplier = instantSupplier;
        this.zoneId = zoneId;
    }

    /** {@inheritDoc} */
    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    /** {@inheritDoc} */
    @Override
    public Clock withZone(final ZoneId zoneId) {
        if (Objects.equals(this.zoneId, zoneId)) {
            return this;
        }

        return new ManualClock(supplier, zoneId);
    }

    /** {@inheritDoc} */
    @Override
    public Instant instant() {
        return supplier.get();
    }
}
