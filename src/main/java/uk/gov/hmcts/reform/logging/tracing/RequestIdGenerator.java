package uk.gov.hmcts.reform.logging.tracing;

import java.util.UUID;

public class RequestIdGenerator {

    public static String next() {
        return UUID.randomUUID().toString();
    }

}
