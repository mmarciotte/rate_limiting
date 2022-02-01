package com.mm.rate_limit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mm.rate_limit.model.FoassMessage;
import com.mm.rate_limit.service.FoassService;
import com.mm.rate_limit.service.RateLimiter;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    final FoassService foassService;
    final RateLimiter rateLimiter;

    public MessageController(FoassService foassService, RateLimiter rateLimiter) {
        this.foassService = foassService;
        this.rateLimiter = rateLimiter;
    }

    @Timed(value = "endpoint.message.time", description = "Time taken to return message")
    @GetMapping("/message")
    public ResponseEntity<String> message(@RequestHeader(value = "userId", required = false) String userId) throws JsonProcessingException {

        ResponseEntity<String> responseEntity = null;

        if (userId != null) {
            if (rateLimiter.isAllowed(userId)) {
                FoassMessage message = foassService.getMessage();
                if (message != null) {
                    responseEntity = new ResponseEntity<>(message.toJson(), HttpStatus.OK);
                }
            } else {
                responseEntity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
            }
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    @ExceptionHandler({JsonProcessingException.class})
    public void handleException(JsonProcessingException e) {
        e.printStackTrace();
    }
}
