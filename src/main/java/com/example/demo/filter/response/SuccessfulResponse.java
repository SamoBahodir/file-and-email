package com.example.demo.filter.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class SuccessfulResponse<T> {
    private Integer status;
    private ResponseMessage message;
    private T data;


    public SuccessfulResponse(T data) {
        this.data = data;
        this.status = 0;
    }

    public SuccessfulResponse(T data, HttpStatus status) {
        this.data = data;
        this.status = 0;
    }

    public SuccessfulResponse() {
        this.status = 0;
    }

}

