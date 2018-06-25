package com.textanaliser.server.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;
@Service
public class UploadFileService {
    Path rootlocation = Paths.get("uploadFiles");
    public void copyFile(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), rootlocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Can not  to copy file");
        }
    }
    public Resource loadFile(String filename){
        Path file = rootlocation.resolve(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()){
                return resource;
            }
            throw new RuntimeException("failure");
        } catch (MalformedURLException e) {
          throw new RuntimeException("failure");
        }
    }

}
