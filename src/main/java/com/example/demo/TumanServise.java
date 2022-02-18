package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class TumanServise {
    private final TumanRepository tumanRepository;

    public TumanServise(TumanRepository tumanRepository) {
        this.tumanRepository = tumanRepository;
    }
    public Tuman save(Tuman tuman){
        return tumanRepository.save(tuman);
    }
}
