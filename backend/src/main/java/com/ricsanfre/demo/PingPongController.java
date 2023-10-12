package com.ricsanfre.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    record PingPong (String result){}
    // Exposing endpoint for checking the server is alive
    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Pong");
    }
}
