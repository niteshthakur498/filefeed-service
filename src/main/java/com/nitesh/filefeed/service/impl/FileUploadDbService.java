package com.nitesh.filefeed.service.impl;

import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FileUploadDbService implements FileUploadService {


    private final FileRepository fileRepository;

    @Override
    public Mono<FileEntity> uploadFile(MultipartFile file) {
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setFileData(file.getBytes());
            return fileRepository.save(fileEntity);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to upload file", e));
        }
    }
}
