package it.mulders.clocky;

import java.time.Duration;

class TestUtils {
    private TestUtils() {
    }

    @SuppressWarnings({
            "java:S2925" // "Thread.sleep" should not be used in tests
    })
    static void sleep(Duration duration) throws InterruptedException {
        Thread.sleep(duration.toMillis());
    }
}
