package com.example.demo.filter.page;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class PageableRequestUtilPage {
    public static Pageable toPageablePage(PageableRequestPage pageable) {
        return PageRequest.of(
                pageable.getPage(),
                pageable.getPerPage());

    }
}
