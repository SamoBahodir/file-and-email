package com.example.demo.fileStorage;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
public class FileStorageService {
    private final FileStorageRepository fileStorageRepository;
    @Value("${upload.folder}")
    private String uploadFolder;
    private final Hashids hashids;

    public FileStorageService(FileStorageRepository fileStorageRepository) {
        this.fileStorageRepository = fileStorageRepository;
        this.hashids=new Hashids(getClass().getName(),6);
    }

    @Transactional
    public void save(MultipartFile multipartFile) {
        FileStorage fileStorage = new FileStorage();
        fileStorage.setName(multipartFile.getOriginalFilename());
        fileStorage.setExtension(getExt(multipartFile.getOriginalFilename()));
        fileStorage.setContentType(multipartFile.getContentType());
        fileStorage.setSize(multipartFile.getSize());
        fileStorage.setFileStorageStatus(FileStorageStatus.DRAFT);
        fileStorageRepository.save(fileStorage);
        fileStorageRepository.save(fileStorage);

        Date now = new Date();
        File uploadFolder = new File(String.format("%s/upload_folder/%d/%d/%d", this.uploadFolder,
                1900 + now.getTime(),
                1 + now.getMonth(),
                now.getTime()));
        if (!uploadFolder.exists()&&uploadFolder.mkdirs()){
            System.out.println("aytilgan papkalar yartildi");
        }
        fileStorage.setHashId(hashids.encode(fileStorage.getId()));
        fileStorage.setUploadPath(String.format("upload_folder/%d/%d/%d/%s.%s",
                1900 + now.getTime(),
                1 + now.getMonth(),
                now.getTime(),
                fileStorage.getHashId(),
                fileStorage.getExtension()));
        fileStorageRepository.save(fileStorage);
        uploadFolder=uploadFolder.getAbsoluteFile();
        File file=new File(uploadFolder,String.format("%s.%s",fileStorage.getHashId(),fileStorage.getExtension()));
        try {
            multipartFile.transferTo(file);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getExt(String fileName) {
        String ext = null;
        if (fileName != null && !fileName.isEmpty()) {
            int dot = fileName.lastIndexOf('.');
            if (dot > 0 && dot <= fileName.length() - 2) {
                ext = fileName.substring(dot+1);
            }
        }
        return ext;
    }
}
