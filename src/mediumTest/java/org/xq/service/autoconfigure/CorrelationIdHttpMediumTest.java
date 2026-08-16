package org.xq.service.autoconfigure;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CorrelationIdHttpMediumTest {
    @Test
    public void emitsGeneratedCorrelationHeaderForLocalRequest() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter(CorrelationIdGenerators.fixed("medium-test-id"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, (request, ignoredResponse) -> {
            assertEquals(CorrelationContext.currentId().orElseThrow(), "medium-test-id");
        });

        assertEquals(response.getHeader(CorrelationIdFilter.HEADER_NAME), "medium-test-id");
    }
}
