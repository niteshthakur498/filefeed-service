package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
                    log.error("Error during file processing: {}", e.getMessage());
                    return Mono.just("Failed to upload file: " + e.getMessage());
                });
    }

    @GetMapping("/download/{id}")
    public Mono<ResponseEntity<DataBuffer>> getFileById(@PathVariable Long id) {
        return fileUploadService.getFileById(id)
                .flatMap(fileEntity -> {
                    HttpHeaders headers = new HttpHeaders();
                    String contentType = fileEntity.getContentType();
                    headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));
                    headers.setContentDispositionFormData("attachment", fileEntity.getFilename());

                    // Create DataBufferFactory
                    DataBufferFactory factory = new DefaultDataBufferFactory();

                    // Create a DataBuffer from byte[] (wrap the byte[] into DataBuffer)
                    DataBuffer dataBuffer = factory.wrap(fileEntity.getFileData());  // wrap byte[] into DataBuffer

                    return Mono.just(ResponseEntity.ok()
                            .headers(headers)
                            .body(dataBuffer));  // Returning DataBuffer for reactive processing
                })
                .onErrorResume(e -> {
                    log.error("Error during file retrieval: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                });
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteFile(@PathVariable Long id) {
        log.info("Request to delete file with ID: {}", id);
        return fileUploadService.deleteFile(id)
                .map(deletedMessage -> ResponseEntity.ok(deletedMessage))
                .onErrorResume(e -> {
                    log.error("Error deleting file: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to delete file: " + e.getMessage()));
                });
    }
}
