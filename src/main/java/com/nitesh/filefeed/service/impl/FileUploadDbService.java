package com.nitesh.filefeed.service.impl;

import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadDbService implements FileUploadService {


    private final FileRepository fileRepository;

    @Override
    public Mono<FileEntity> processAndSaveFile(Mono<FilePart> file) {
        return file.flatMap(filePart -> {
            log.error("Entered Here....." + filePart.filename());

            // Extract content type
            String contentType = StringUtils.isEmpty(filePart.headers().getContentType().toString()) ? "application/octet-stream" : filePart.headers().getContentType().toString();

            // Process file content (combine chunks into byte array)
            return DataBufferUtils.join(filePart.content())
                    .map(dataBuffer -> {
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
                        return fileRepository.save(fileEntity);
                    });
        });
    }

    @Override
    public Mono<FileEntity> getFileById(Long id) {
        return fileRepository.findById(id)
                .switchIfEmpty(Mono.error(new FileNotFoundException("File with ID " + id + " not found."))) // Custom error if file not found
                .onErrorMap(e -> {
                    log.error("Error while retrieving file: {}", e.getMessage());
                    return new RuntimeException("Failed to retrieve file due to internal error.");
                });
    }

    @Override
    public Mono<String> deleteFile(Long id) {
        log.info("Attempting to delete file with ID: " + id);

        // Check if file exists in the database
        return fileRepository.findById(id)
                .flatMap(fileEntity -> {
                    // Delete file and return a success message
                    return fileRepository.delete(fileEntity)
                            .then(Mono.just("File deleted successfully: " + fileEntity.getFilename()));
                })
                .switchIfEmpty(Mono.error(new FileNotFoundException("File with ID " + id + " not found."))) // Custom error if file not found
                .onErrorMap(e -> {
                    log.error("Error while deleting file: " + e.getMessage());
                    return new RuntimeException("Failed to delete file due to internal error.");
                });
    }
}
