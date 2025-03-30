package com.nitesh.filefeed.service.impl;

import com.nitesh.filefeed.exception.FileNotReceivedException;
import com.nitesh.filefeed.exception.UnsupportedFileFormatException;
import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import com.nitesh.filefeed.utils.FileFormatValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadDbService implements FileUploadService {

    private final FileRepository fileRepository;
    private final FileFormatValidator fileFormatValidator;

    @Override
    public Mono<FileEntity> processAndSaveFile(Mono<FilePart> file) {
        return file
                .switchIfEmpty(Mono.error(new FileNotFoundException("No file provided")))
                .flatMap(filePart -> {
                    if(filePart.filename().isEmpty()){
                        return Mono.error(new FileNotReceivedException("No file provided"));
                    }
                    // Extract content type (default to "application/octet-stream" if not provided)
                    String contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString().isEmpty() ? "application/octet-stream" : filePart.headers().getContentType().toString();
                    if(!fileFormatValidator.isValidFormat(filePart.filename())){
                        throw new UnsupportedFileFormatException("UnSupported File Format....");
                    }
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
                    return e;
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
                    return e;
                });
    }
}
