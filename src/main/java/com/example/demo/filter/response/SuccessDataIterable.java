package com.example.demo.filter.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SuccessDataIterable<T> {
    private Integer status;
    private ResponseMessage message;
    private List<T> data;
    private int totalPages;
    private long totalCount;
    private int size;
    private int page;

    public SuccessDataIterable(Page<T> objects) {
        this.status = 0;
        this.message = new ResponseMessage();
        this.data = new ArrayList<>();
        objects.forEach(this.data::add);
        this.totalPages = objects.getTotalPages();
        this.totalCount = objects.getTotalElements();
        this.size = objects.getSize();
        this.page = objects.getNumber();
    }

}
