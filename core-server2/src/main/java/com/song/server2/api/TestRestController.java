package com.song.server2.api;

import com.song.server2.service.TestService;
import com.song.server2.util.CustomEventLog;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/server2")
public class TestRestController {

    private final TestService testService;

    public TestRestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public Object test(@RequestParam Long systemCodeId) {
        return testService.getBySystemCodeId(systemCodeId);
    }

    @CustomEventLog(domain = "KVMS", menu = "선박탐지분류결과", action = "GET")
    @GetMapping("/{id}")
    public Object test2(@PathVariable Long id) {
        return testService.get(id);
    }
}
