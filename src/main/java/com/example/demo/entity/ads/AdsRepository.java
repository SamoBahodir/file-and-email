package com.example.demo.entity.ads;

import com.example.demo.entity.Ads;
import com.example.demo.filter.JpaGenericRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdsRepository extends JpaRepository<Ads, Long>, JpaGenericRepository<Ads> {
}
