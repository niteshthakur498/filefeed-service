package com.nitesh.filefeed.service;

import com.nitesh.filefeed.model.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileUploadService {
    public Mono<FileEntity> uploadFile(MultipartFile file);
}
