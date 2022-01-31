package com.mm.rate_limit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mm.rate_limit.model.FoassMessage;
import com.mm.rate_limit.service.FoassService;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    final
    FoassService foassService;

    public MessageController(FoassService foassService) {
        this.foassService = foassService;
    }

    @Timed(value = "endpoint.message.time", description = "Time taken to return message")
    @GetMapping("/message")
    public String message() {

        try {
            FoassMessage message = foassService.getMessage();
            if (message != null) {
                return message.toJson();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
