package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.dto.FileMetadata;
import com.nitesh.filefeed.dto.ResponseWrapper;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileUploadService fileUploadService;

    /**
     * Endpoint to upload a file.
     */
    @PostMapping("/upload")
    public Mono<ResponseEntity<ResponseWrapper<FileMetadata>>> uploadFile(Mono<FilePart> file) {
        return fileUploadService.processAndSaveFile(file)
                .doOnSuccess(savedFile -> log.info("File uploaded successfully: {}", savedFile.getFilename()))
                .map(savedFile -> {
                    // Success response wrapped in ResponseWrapper
                    ResponseWrapper<FileMetadata> response = new ResponseWrapper<>(
                            HttpStatus.OK.value(),
                            "File uploaded successfully",
                            FileMetadata.builder().id(savedFile.getId().toString()).fileName(savedFile.getFilename()).build(),
                            Collections.emptyList()
                    );
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .onErrorResume(e -> {
                    log.error("Error during file processing: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    /**
     * Endpoint to download a file by ID.
     */
    @GetMapping("/{id}/download")
    public Mono<ResponseEntity<DataBuffer>> getFileById(@PathVariable Long id) {
        return fileUploadService.getFileById(id)
                .flatMap(fileEntity -> {
                    log.debug("received file .....{}", fileEntity.getFilename());
                    HttpHeaders headers = new HttpHeaders();
                    String contentType = fileEntity.getContentType();
                    headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));
                    headers.setContentDispositionFormData("attachment", fileEntity.getFilename());


                    // Create a DataBuffer from byte[] (wrap the byte[] into DataBuffer)
                    DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(fileEntity.getFileData());  // wrap byte[] into DataBuffer

                    log.info("File retrieved successfully: {}", fileEntity.getFilename());
                    return Mono.just(ResponseEntity.ok()
                            .headers(headers)
                            .body(dataBuffer));  // Returning DataBuffer for reactive processing
                })
                .onErrorResume(e -> {
                    log.error("Error during file retrieval: {}", e.getMessage());
                    return Mono.error(e);
                });
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<FileMetadata>>> getFileMetadata(@PathVariable Long id, ServerHttpRequest request) {
        return fileUploadService.getFileById(id)
                .flatMap(fileEntity -> {

                    String downloadUrl = UriComponentsBuilder.fromUri(request.getURI())
                            .path("/download")
                            .build()
                            .toString();

                    ResponseWrapper<FileMetadata> response = new ResponseWrapper<>(
                            HttpStatus.OK.value(),
                            "File uploaded successfully",
                            FileMetadata.builder().id(fileEntity.getId().toString())
                                    .fileName(fileEntity.getFilename())
                                    .contentType(fileEntity.getContentType())
                                    .url(downloadUrl)
                                    .build(),
                            Collections.emptyList()
                    );

                    return Mono.just(ResponseEntity.ok(response)); // Return metadata wrapped in ResponseEntity
                })
                .onErrorResume(e -> {
                    log.error("Error retrieving file metadata: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(null)); // Return 404 if the file is not found
                });
    }



    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseWrapper<String>>> deleteFile(@PathVariable Long id) {
        log.info("Request to delete file with ID: {}", id);
        return fileUploadService.deleteFile(id)
                .map(deletedMessage -> {
                    log.info("File deleted successfully: {}", id);
                    // Success response wrapped in ResponseWrapper
                    ResponseWrapper<String> response = new ResponseWrapper<>(
                            HttpStatus.OK.value(),
                            "File deleted successfully",
                            deletedMessage,
                            Collections.emptyList()
                    );
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    log.error("Error deleting file: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
