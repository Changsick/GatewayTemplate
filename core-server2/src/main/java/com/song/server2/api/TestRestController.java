package com.song.server2.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server2")
public class TestRestController {

    @GetMapping
    public String test() {
        return "server2 - SUCCESS";
    }
}
