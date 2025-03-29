package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public Mono<String> uploadFile(Mono<FilePart> file) {
        return fileUploadService.processAndSaveFile(file)
                .map(savedFile -> "File uploaded successfully: " + savedFile.getId() + "--" + savedFile.getFilename())
                .onErrorResume(e -> {
                    log.error("Error during file processing: " + e.getMessage());
                    return Mono.just("Failed to upload file: " + e.getMessage());
                });
    }


    @GetMapping("/{id}")
    public Mono<byte[]> getFileById(@PathVariable Long id) {
        log.error("ENtered 2.......................");
        return fileUploadService.getFileById(id)
                .map(fileEntity -> {
                    // Set appropriate headers to indicate that the response is a file download
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", fileEntity.getFilename());

                    return fileEntity.getFileData();  // Returns the byte[] representing the file content
                })
                .onErrorResume(e -> Mono.empty());  // Handle file not found or any error
    }
}