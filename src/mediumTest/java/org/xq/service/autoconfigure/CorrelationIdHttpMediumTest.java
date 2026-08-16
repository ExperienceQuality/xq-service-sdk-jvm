package org.xq.service.autoconfigure;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SpringBootTest(
        classes = CorrelationIdHttpMediumTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "xq.service.name=correlation-medium-test"
)
public class CorrelationIdHttpMediumTest extends AbstractTestNGSpringContextTests {
    @LocalServerPort
    private int port;

    @Test
    public void echoesAValidInboundCorrelationHeaderOverLocalhostHttp() {
        ResponseEntity<Void> response = RestClient.create()
                .get()
                .uri("http://localhost:" + port + "/correlation")
                .header(CorrelationIdFilter.HEADER_NAME, "customer-request-42")
                .retrieve()
                .toBodilessEntity();

        assertEquals(response.getStatusCode().value(), 204);
        assertEquals(response.getHeaders().getFirst(CorrelationIdFilter.HEADER_NAME), "customer-request-42");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
        @Bean
        CorrelationController correlationController() {
            return new CorrelationController();
        }
    }

    @RestController
    static class CorrelationController {
        @GetMapping("/correlation")
        ResponseEntity<Void> correlation() {
            return ResponseEntity.noContent().build();
        }
    }
}
