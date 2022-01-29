package com.mm.rate_limit.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mm.rate_limit.model.FoassMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(FoassService.class)
public class FoassServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FoassService foassService;

    private MockRestServiceServer mockServer;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void mockedExternalServiceRun_ok() throws URISyntaxException, JsonProcessingException {

        FoassMessage message = FoassMessage.builder().message("Not Cool story, bro.").subtitle("- another").build();

        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://www.foaas.com/cool/matt")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(message.toJson())
                );

        FoassMessage response = foassService.getMessage();
        mockServer.verify();
        assertEquals(message, response);
    }
}
