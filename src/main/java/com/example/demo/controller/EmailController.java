package com.example.demo.controller;

import com.example.demo.email.EmailPayload;
import com.example.demo.email.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {
    private final EmailSenderService service;

    @PostMapping("/send")
    public ResponseEntity sendEmail(@RequestBody EmailPayload payload) {
        this.service.sendEmail(payload.getTo(), payload.getSubject(), payload.getMessage());
        return ResponseEntity.ok("success");
    }
}
