package com.textanaliser.server.controller;

import com.textanaliser.server.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin
public class UploadFileController {
    @Autowired
    UploadFileService uploadFileService;
    List<String> uploadFiles = new ArrayList<>();
    String message;

    @PostMapping("/uploadfile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        try{
        uploadFileService.copyFile(file);
        uploadFiles.add(file.getOriginalFilename());
        message = file.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.OK).body(message);
    } catch (Exception e) {
        message = "FAIL to upload " + file.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
    }
    }
    @GetMapping("/getallfiles")
    public ResponseEntity<List<String>> getListFiles(){
        ArrayList<String> listfile = new ArrayList();
        Path dir =  Paths.get(".\\uploadFiles");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                listfile.add(file.getFileName().toString());
                System.out.println(file.getFileName());
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        List<String> filesname=uploadFiles
				.stream().map(fileName -> MvcUriComponentsBuilder
                .fromMethodName(UploadFileController.class, "getFile", fileName).build().toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(listfile);
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = uploadFileService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
