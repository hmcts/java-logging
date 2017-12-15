package uk.gov.hmcts.reform.logging.tracing;

import java.util.UUID;

public final class RequestIdGenerator {

    public static String next() {
        return UUID.randomUUID().toString();
    }

    private RequestIdGenerator() {
        // Utility class
    }

}
