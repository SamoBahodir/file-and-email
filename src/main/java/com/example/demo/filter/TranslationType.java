package com.example.demo.filter;

import lombok.Getter;

@Getter
public enum TranslationType {

    ALL("ALL"),
    ADMIN("Admin cabinet"),
    FRONT("Front");

    private final String title;

    TranslationType(String title) {
        this.title = title;
    }
}
