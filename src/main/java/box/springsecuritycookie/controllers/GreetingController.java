package box.springsecuritycookie.controllers;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/greeting")
@SpringBootApplication
public class GreetingController {

    @GetMapping
    public ResponseEntity<Map<String, String>> handle() {
        return ResponseEntity.ok()
                .body(Map.of("greeting", "Hello"));
    }
}
