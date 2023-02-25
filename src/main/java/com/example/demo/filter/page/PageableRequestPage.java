package com.example.demo.filter.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageableRequestPage {
//    @Schema(name = "per_page",
//            description = "На страницу",
//            example = "10")
    private int perPage = 10;

//    @Schema(description = "Страница", example = "1")
    private int page = 0;

}
