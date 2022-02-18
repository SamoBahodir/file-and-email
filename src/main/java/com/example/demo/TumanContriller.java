package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TumanContriller {
    private final TumanServise tumanServise;

    public TumanContriller(TumanServise tumanServise) {
        this.tumanServise = tumanServise;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody Tuman tuman) {
        Tuman tuman1 = tumanServise.save(tuman);
        return ResponseEntity.ok(tuman1);
    }
}
