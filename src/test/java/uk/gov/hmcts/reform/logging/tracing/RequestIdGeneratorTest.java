package uk.gov.hmcts.reform.logging.tracing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestIdGeneratorTest {

    @Test
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public void nextIdShouldReturnUUIDString() {
        String id = RequestIdGenerator.next();
        assertThat(id).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    @Test
    public void shouldGenerateDifferentValuesOnEachCall() {
        String first = RequestIdGenerator.next();
        String second = RequestIdGenerator.next();
        String third = RequestIdGenerator.next();

        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(third);
        assertThat(third).isNotEqualTo(first);
    }

}
