package com.example.demo.fileStorage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.*;
@Getter
@Setter
@Entity
public class FileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String extension;
    private String hashId;
    private Long size;
    private String contentType;
    @Enumerated(EnumType.STRING)
    private FileStorageStatus fileStorageStatus;
    private String uploadPath;


}
