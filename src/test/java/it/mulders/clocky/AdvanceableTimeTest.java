package it.mulders.clocky;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class AdvanceableTimeTest implements WithAssertions {

    @Test
    void instant_should_return_value_at_construction_time() {
        final AdvanceableTime advanceableTime = new AdvanceableTime(Instant.EPOCH);

        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH);
    }

    @Test
    void allows_manual_progression() throws InterruptedException {
        final AdvanceableTime advanceableTime = new AdvanceableTime(Instant.EPOCH);

        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH);
        TestUtils.sleep(Duration.ofMillis(10));
        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH);

        advanceableTime.advanceBy(Duration.ofHours(2));

        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));
        TestUtils.sleep(Duration.ofMillis(10));
        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));

        advanceableTime.advanceBy(Duration.ZERO);

        assertThat(advanceableTime.instant()).isEqualTo(Instant.EPOCH.plus(2, ChronoUnit.HOURS));
    }

    @Test
    void disallows_negative_progression() {
        final AdvanceableTime advanceableTime = new AdvanceableTime(Instant.EPOCH);

        assertThatThrownBy(() -> advanceableTime.advanceBy(Duration.ofMillis(-1))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_contract() {
        EqualsVerifier.forClass(AdvanceableTime.class)
                .withNonnullFields("instant")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}
