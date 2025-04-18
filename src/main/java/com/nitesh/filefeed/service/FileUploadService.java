package com.nitesh.filefeed.service;

import com.nitesh.filefeed.model.entity.FileEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileUploadService {
    public Mono<FileEntity> processAndSaveFile(String externalReference, Mono<FilePart> file);

    public Mono<FileEntity> getFileById(Long id);

    public Mono<String> deleteFile(Long id);
}
