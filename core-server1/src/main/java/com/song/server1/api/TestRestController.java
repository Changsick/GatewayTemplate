package com.song.server1.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server1")
public class TestRestController {

    @GetMapping
    public String test() {
        return "server1 - SUCCESS";
    }
}
