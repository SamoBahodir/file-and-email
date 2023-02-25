package com.example.demo.filter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@NoRepositoryBean
public interface JpaGenericRepository<T> extends JpaSpecificationExecutor<T> {
    Page<T> findAll(@Nullable Pageable pageable);

    @NonNull
    List<T> findAll(@Nullable Specification<T> specification);

    @NonNull
    Page<T> findAll(@Nullable Specification<T> specification, @Nullable Pageable pageable);
}
