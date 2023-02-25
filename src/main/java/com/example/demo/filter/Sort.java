package com.example.demo.filter;

import com.google.common.base.CaseFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sort {
    @Schema(description = "Имя полей", example = "id")
    private String name = "id";

    @Schema(description = "Сортировать по направлению", example = "desc")
    private String direction = "desc";

    public String getName() {
        if (name.split("_").length == 1)
            return name;
        return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name));
    }
}
