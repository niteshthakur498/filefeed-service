package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileService;

    @PostMapping("/upload")
    public Mono<String> uploadFile(@RequestPart("file") MultipartFile file) {
        return fileService.uploadFile(file)
                .map(fileEntity -> "File uploaded successfully: " + fileEntity.getFilename())
                .onErrorResume(e -> Mono.just("Failed to upload file: " + e.getMessage()));
    }
}
