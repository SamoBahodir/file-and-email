package com.example.demo.filter;

import com.example.demo.filter.response.PageableRequest;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PageableRequestUtil {
    public static Pageable toPageable(PageableRequest pageable) {
        return PageRequest.of(
                pageable.getPage(),
                pageable.getPerPage(),
                Sort.Direction.fromString(pageable.getSort().getDirection()),
                pageable.getSort().getName()
        );
    }

    public static Pageable toPageable(PageableRequest pageable, Sort sort) {
        return PageRequest.of(
                pageable.getPage(),
                pageable.getPerPage(),
                sort
        );
    }

    public static void addStatusActive(PageableRequest pageableRequest) {
        if (pageableRequest.getSearch() == null) {
            pageableRequest.setSearch(new ArrayList<SearchCriteria>());
        }
//        if (!isGlobalFound(pageableRequest.getSearch())) {
        pageableRequest.getSearch().add(new SearchCriteria("status", "=", "ACTIVE"));
//        }

    }

    private static boolean isGlobalFound(List<SearchCriteria> searchCriteriaList) {

        for (SearchCriteria searchCriteria : searchCriteriaList) {
            if (searchCriteria.getIsGlobal()) {
                return true;
            }
        }
        return false;
    }

}
