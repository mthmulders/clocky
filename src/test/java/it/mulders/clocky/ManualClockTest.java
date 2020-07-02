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

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

class ManualClockTest implements WithAssertions {
    // The Clock javadoc states that instances of Clock must be final, immutable and thread-safe.

    private static final Supplier<Instant> EPOCH_SUPPLIER = () -> Instant.EPOCH;

    @SuppressWarnings({
            "java:S2925" // "Thread.sleep" should not be used in tests
    })
    private void sleep(Duration duration) throws InterruptedException {
        Thread.sleep(duration.toMillis());
    }

    @Test
    void instant_should_return_value_at_construction_time() throws InterruptedException {
        final Clock clock = new ManualClock(EPOCH_SUPPLIER);

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH);

        sleep(Duration.ofMillis(10));

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void millis_should_return_value_at_construction_time() {
        final Clock clock = new ManualClock(EPOCH_SUPPLIER);

        assertThat(clock.millis()).isEqualTo(Instant.EPOCH.toEpochMilli());
    }

    @Test
    void constructor_should_use_system_default_zone() {
        final Clock clock = new ManualClock(EPOCH_SUPPLIER);

        assertThat(clock.getZone()).isEqualTo(ZoneOffset.systemDefault());
    }

    @Test
    void constructor_should_override_zone() {
        final ZoneOffset offset = ZoneOffset.ofHoursMinutes(4, 30);
        final Clock clock = new ManualClock(EPOCH_SUPPLIER, offset);

        assertThat(clock.getZone()).isEqualTo(offset);
    }

    @Test
    void with_same_zone_return_same_clock() {
        final Clock clock = new ManualClock(EPOCH_SUPPLIER);

        final Clock newClock = clock.withZone(ZoneOffset.systemDefault());

        assertThat(newClock).isSameAs(clock);
    }

    @Test
    void with_other_zone_return_new_clock() {
        final ZoneOffset offset = ZoneOffset.ofHoursMinutes(4, 30);
        final Clock clock = new ManualClock(EPOCH_SUPPLIER);

        final Clock newClock = clock.withZone(offset);

        assertThat(newClock).isNotSameAs(clock);
        assertThat(newClock.getZone()).isEqualTo(offset);
    }

    @Test
    void allows_manual_progression() throws InterruptedException {
        final long initialValue = Instant.now().toEpochMilli();
        final long updatedValue = initialValue + 10_000L;
        final AtomicLong instant = new AtomicLong();
        instant.set(initialValue);

        final Clock clock = new ManualClock(() -> Instant.ofEpochMilli(instant.get()));

        assertThat(clock.millis()).isEqualTo(initialValue);
        sleep(Duration.ofMillis(10));
        assertThat(clock.millis()).isEqualTo(initialValue);

        instant.set(updatedValue);

        assertThat(clock.millis()).isEqualTo(updatedValue);
        sleep(Duration.ofMillis(10));
        assertThat(clock.millis()).isEqualTo(updatedValue);
    }
}