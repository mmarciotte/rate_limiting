package com.mm.rate_limit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mm.rate_limit.model.FoassMessage;
import com.mm.rate_limit.service.FoassService;
import com.mm.rate_limit.service.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerIntegrationTest {

    @MockBean
    FoassService foassService;

    @MockBean
    RateLimiter rateLimiter;

    @LocalServerPort
    int port;
    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void getMessageWorks() throws JsonProcessingException {

        FoassMessage message = FoassMessage.builder().message("ok").subtitle("st").build();
        when(foassService.getMessage()).thenReturn(message);
        when(rateLimiter.isAllowed(any())).thenReturn(true);

        client.get().uri("/message").header("userId", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(message.toJson())
                .consumeWith(result -> assertThat(result.getResponseBody()).isNotEmpty());
    }
}