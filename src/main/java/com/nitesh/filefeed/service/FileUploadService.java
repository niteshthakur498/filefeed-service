package com.nitesh.filefeed.service;

import com.nitesh.filefeed.model.entity.FileEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileUploadService {
    public Mono<FileEntity> uploadFile(Mono<FilePart> file);

    public Mono<FileEntity> getFileById(Long id);
}
