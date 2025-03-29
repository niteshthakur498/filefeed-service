package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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


    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<byte[]>> getFileById(@PathVariable Long id) {
        return fileUploadService.getFileById(id)
                .map(fileEntity -> {
                    HttpHeaders headers = new HttpHeaders();
                    String contentType = fileEntity.getContentType();
                    headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));
                    headers.setContentDispositionFormData("attachment", fileEntity.getFilename());

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(fileEntity.getFileData());  // Returns the byte[] representing the file content
                })
                .onErrorResume(e -> {
                    log.error("Error during file retrieval: {}", e.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });  // Handle file not found or any error
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteFile(@PathVariable Long id) {
        log.info("Request to delete file with ID: " + id);

        // Call the service method for deleting the file
        return fileUploadService.deleteFile(id)
                .map(ResponseEntity::ok) // On success, return HTTP 200 with message
                .onErrorResume(e -> {
                    log.error("Error deleting file: " + e.getMessage());
                    // If error occurs, return HTTP 500 with error message
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to delete file: " + e.getMessage()));
                });
    }
}