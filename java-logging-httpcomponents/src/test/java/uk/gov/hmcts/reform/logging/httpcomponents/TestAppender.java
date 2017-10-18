package uk.gov.hmcts.reform.logging.httpcomponents;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TestAppender extends AppenderBase<ILoggingEvent> {
    private final List<ILoggingEvent> events = new ArrayList<>();

    TestAppender() {
        start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        events.add(eventObject);
    }

    public void assertEvent(int index, Level expectedLevel, String expectedMessage, Marker marker) {
        assertThat(event(index).getLevel()).isEqualTo(expectedLevel);
        assertThat(event(index).getFormattedMessage()).isEqualTo(expectedMessage);
        assertThat(event(index).getMarker()).isEqualTo(marker);
    }

    public ILoggingEvent event(int index) {
        return events.get(index);
    }
}
