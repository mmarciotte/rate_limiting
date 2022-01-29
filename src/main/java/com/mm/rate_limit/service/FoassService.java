package com.mm.rate_limit.service;

import com.mm.rate_limit.model.FoassMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Foass connection service
 * Provides methods to obtain messages from foass
 */
@Service
public class FoassService {

    private final RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(FoassService.class);

    public FoassService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FoassMessage getMessage() {

        FoassMessage message = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        try {
            String url = "https://www.foaas.com/cool/matt";
            ResponseEntity<FoassMessage> response = this.restTemplate.exchange(url, HttpMethod.GET, request, FoassMessage.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                message = response.getBody();
            }
        } catch (HttpStatusCodeException ex) {
            logger.error(ex.getMessage());
        }
        return message;
    }
}