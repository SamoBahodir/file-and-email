package com.example.demo.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Name {

    private String uz;
    private String ru;
    private String en;

    @JsonIgnore
    public String getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.equals(Locale.ENGLISH))
            return en;
        else if (locale.equals(Locale.forLanguageTag("uz"))) {
            return uz;
        } else
            return ru;
    }

    @JsonIgnore
    public String getLocale(String lang) {
        return switch (lang) {
            case "uz" -> uz;
            case "ru" -> ru;
            default -> en;
        };
    }}
