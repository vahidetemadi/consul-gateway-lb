package com.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

	@Test
	void testLoadBalancingViaConsul() {
        List<String> responses = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> {
            String body = webTestClient.get()
                    .uri("/api/v1/test")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();
            responses.add(body);
        });
    // Assumption: consul-demo-1 and consul-demo-2 represent different instances of consul-demo Spring boot app
    assertThat(responses.stream().filter(s -> s.contains("consul-demo-1")).toList())
            .hasSameSizeAs(responses.stream().filter(s -> s.contains("consul-demo-2")).toList());
	}

}
