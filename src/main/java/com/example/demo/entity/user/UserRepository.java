package com.example.demo.entity.user;

import com.example.demo.filter.JpaGenericRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, JpaGenericRepository<User> {
}
