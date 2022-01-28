package com.mm.rate_limit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
        @GetMapping("/message")
        public String message() {
            return "Some message";
        }
}
