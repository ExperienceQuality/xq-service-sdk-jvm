package org.xq.service.autoconfigure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServiceErrorHandlerTest {
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
            .setControllerAdvice(new ServiceErrorHandler())
            .build();

    @AfterEach
    void clearContext() {
        CorrelationContext.clear();
    }

    @Test
    void exposesSafeProblemWithCorrelationId() throws Exception {
        CorrelationContext.set("request-123");

        mockMvc.perform(get("/failure"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.correlationId").value("request-123"));
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        CorrelationContext.set("request-123");

        mockMvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INFRASTRUCTURE_ERROR"))
                .andExpect(jsonPath("$.detail").value("The service could not complete the request."))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensitive"))));
    }

    @Controller
    static class FailingController {
        @GetMapping("/failure")
        void failure() {
            throw new ServiceException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Input is invalid");
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("sensitive infrastructure detail");
        }
    }
}
