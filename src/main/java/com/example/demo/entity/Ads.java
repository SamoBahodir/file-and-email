package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "telegram_ads")
public class Ads {
    @Id
    @GeneratedValue(generator = "ads_id_seq", strategy = GenerationType.AUTO    )
    private Long id;
    private String ad;
}
