package uk.gov.hmcts.reform.logging.httpcomponents;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

class FakeClock extends Clock {
    private long elapsed;
    private final long increment;

    /* default */ FakeClock(int increment) {
        super();
        this.increment = increment;
    }

    @Override
    public ZoneId getZone() {
        return null;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return null;
    }

    @Override
    public Instant instant() {
        elapsed += increment;
        return Instant.ofEpochMilli(elapsed);
    }
}
