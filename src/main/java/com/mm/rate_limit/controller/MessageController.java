package com.mm.rate_limit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mm.rate_limit.model.FoassMessage;
import com.mm.rate_limit.service.FoassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    FoassService foassService;

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
