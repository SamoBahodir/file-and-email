package com.example.demo.controller;

import com.example.demo.entity.file.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileResource {
    private final FileService fileService;

    public FileResource(FileService fileStorageService) {
        this.fileService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity upload(@RequestParam("file") MultipartFile multipartFile) {
        fileService.save(multipartFile);
        return ResponseEntity.ok(multipartFile.getOriginalFilename() + " file saqlandi");
    }
}
