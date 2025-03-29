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

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadDbService implements FileUploadService {

    private final FileRepository fileRepository;

    @Override
    public Mono<FileEntity> processAndSaveFile(Mono<FilePart> file) {
        return file.flatMap(filePart -> {
            // Extract content type (default to "application/octet-stream" if not provided)
            String contentType = StringUtils.isEmpty(filePart.headers().getContentType().toString()) ? "application/octet-stream" : filePart.headers().getContentType().toString();

            // Use DataBufferUtils to collect the file content
            return DataBufferUtils.join(filePart.content()) // Collects all data buffers into a single buffer
                    .map(dataBuffer -> {
                        // Convert DataBuffer into byte[] (use direct byte[] conversion, avoiding deprecated methods)
                        byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(fileBytes); // Read the content into the byte array
                        return fileBytes; // Return the byte array
                    })
                    .flatMap(fileBytes -> {
                        // Create FileEntity and set the properties
                        FileEntity fileEntity = new FileEntity();
                        fileEntity.setFileData(fileBytes);
                        fileEntity.setFilename(filePart.filename());
                        fileEntity.setContentType(contentType);

                        // Save the file entity to the repository
                        return fileRepository.save(fileEntity);  // This returns a Mono<FileEntity>
                    });
        });
    }


    @Override
    public Mono<FileEntity> getFileById(Long id) {
        return fileRepository.findById(id)
                .switchIfEmpty(Mono.error(new FileNotFoundException("File with ID " + id + " not found.")))
                .onErrorMap(e -> {
                    log.error("Error while retrieving file: {}", e.getMessage());
                    return new RuntimeException("Failed to retrieve file due to internal error.");
                });
    }

    @Override
    public Mono<String> deleteFile(Long id) {
        log.info("Attempting to delete file with ID: {}", id);

        return fileRepository.findById(id)
                .flatMap(fileEntity -> {
                    // Delete the file and return a success message
                    return fileRepository.delete(fileEntity)
                            .then(Mono.just("File deleted successfully: " + fileEntity.getFilename()));
                })
                .switchIfEmpty(Mono.error(new FileNotFoundException("File with ID " + id + " not found.")))
                .onErrorMap(e -> {
                    log.error("Error while deleting file: {}", e.getMessage());
                    return new RuntimeException("Failed to delete file due to internal error.");
                });
    }
}
