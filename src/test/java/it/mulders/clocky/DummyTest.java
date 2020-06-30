package it.mulders.clocky;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class DummyTest implements WithAssertions {
    @Test
    void can_construct_instance_of_dummy() {
        assertThat(new Dummy().dummy()).isNotNull();
    }
}