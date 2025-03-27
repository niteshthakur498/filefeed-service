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

    private final FileUploadService fileService;
    private final FileRepository fileRepository;

    @PostMapping("/upload")
    public Mono<String> uploadFile(Mono<FilePart> file) {
        return file.flatMap(filePart -> {
            // Log and extract details from FilePart
            log.error("Entered Here....." + filePart.filename());

            // Extract content type
            String contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString();

            // Process file content (combine chunks into byte array)
            return DataBufferUtils.join(filePart.content())
                    .map(dataBuffer -> {
                        log.error("Pricing to check 213232432434343");
                        byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(fileBytes);
                        return fileBytes;
                    })
                    .flatMap(fileBytes -> {
                        // Create FileEntity and set the properties
                        log.error("Pricing to check 1234");
                        FileEntity fileEntity = new FileEntity();
                        fileEntity.setFileData(fileBytes);
                        fileEntity.setFilename(filePart.filename());
                        fileEntity.setContentType(contentType);

                        // Save file entity to the repository and return the Mono<FileEntity>
                        return fileRepository.save(fileEntity);  // Here we return Mono<FileEntity>
                    })
                    .map(savedFile -> "File uploaded successfully: " + savedFile.getId() + "--"+ savedFile.getFilename())// Process and return success message
                    .onErrorResume(e -> {
                        log.error("Error during file processing: " + e.getMessage());
                        return Mono.just("Failed to upload file: " + e.getMessage());
                    });
        });
    }


    @GetMapping("/{id}")
    public Mono<byte[]> getFileById(@PathVariable Long id) {
        log.error("ENtered 2.......................");
        return fileService.getFileById(id)
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