package com.example.demo;

import lombok.Data;

import javax.persistence.*;

@Data
@Table
@Entity
public class Tuman {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    public Tuman(String name, String email) {
        this.email = email;
        this.name = name;
    }
}
