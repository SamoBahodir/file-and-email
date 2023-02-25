package com.example.demo.entity.file;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String extension;
    private String hashId;
    private Long size;
    private String contentType;
    @Enumerated(EnumType.STRING)
    private FileStatus fileStorageStatus;
    private String uploadPath;


}
