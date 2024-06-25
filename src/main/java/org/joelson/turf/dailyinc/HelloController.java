package org.joelson.turf.dailyinc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/rest/")
    public String restIndex() {
        return "Greetings from Spring Boot!";
    }
}
