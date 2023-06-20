package it.mulders.clocky;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

class AdvanceableClockTest implements WithAssertions {

    @Test
    void instant_should_return_value_at_construction_time() {
        final Clock clock = new AdvanceableClock(Instant.EPOCH);

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void millis_should_return_value_at_construction_time() {
        final Clock clock = new AdvanceableClock(Instant.EPOCH);

        assertThat(clock.millis()).isEqualTo(Instant.EPOCH.toEpochMilli());
    }

    @Test
    void constructor_should_use_system_default_zone() {
        final Clock clock = new AdvanceableClock(Instant.EPOCH);

        assertThat(clock.getZone()).isEqualTo(ZoneOffset.systemDefault());
    }

    @Test
    void constructor_should_override_zone() {
        final ZoneOffset offset = ZoneOffset.ofHoursMinutes(4, 30);
        final Clock clock = new AdvanceableClock(Instant.EPOCH, offset);

        assertThat(clock.getZone()).isEqualTo(offset);
    }

    @Test
    void with_same_zone_return_same_clock() {
        final Clock clock = new AdvanceableClock(Instant.EPOCH);

        final Clock newClock = clock.withZone(ZoneOffset.systemDefault());

        assertThat(newClock).isSameAs(clock);
    }

    @Test
    void with_other_zone_return_new_clock() {
        final ZoneOffset offset = ZoneOffset.ofHoursMinutes(4, 30);
        final Clock clock = new AdvanceableClock(Instant.EPOCH);

        final Clock newClock = clock.withZone(offset);

        assertThat(newClock).isNotSameAs(clock);
        assertThat(newClock.getZone()).isEqualTo(offset);
    }

    @Test
    void allows_manual_progression() throws InterruptedException {
        final AdvanceableClock clock = new AdvanceableClock(Instant.EPOCH);

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH);
        TestUtils.sleep(Duration.ofMillis(10));
        assertThat(clock.instant()).isEqualTo(Instant.EPOCH);

        clock.advanceBy(Duration.ofHours(2));

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));
        TestUtils.sleep(Duration.ofMillis(10));
        assertThat(clock.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));

        clock.advanceBy(Duration.ZERO);

        assertThat(clock.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));
    }

    @Test
    void disallows_negative_progression() {
        final AdvanceableClock clock = new AdvanceableClock(Instant.EPOCH);

        assertThatThrownBy(() -> clock.advanceBy(Duration.ofMillis(-1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_contract() {
        EqualsVerifier.forClass(AdvanceableClock.class)
                .withNonnullFields("instant", "zoneId")
                .withRedefinedSuperclass()
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}
