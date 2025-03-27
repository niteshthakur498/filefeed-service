package com.nitesh.filefeed.service.impl;

import com.nitesh.filefeed.model.entity.FileEntity;
import com.nitesh.filefeed.repository.FileRepository;
import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadDbService implements FileUploadService {


    private final FileRepository fileRepository;

    @Override
    public Mono<FileEntity> uploadFile(Mono<FilePart> file) {
        log.error("Entered here......................." +  file);

        file.doOnNext(fp->log.error("Here->"+fp.filename()))
                .then(Mono.just("jiijijijijijij...."));

        AtomicReference<String> fileName = new AtomicReference<>("");
        AtomicReference<String> contentType = new AtomicReference<>("");

        try {
            file.flatMap(
                    filePart -> {
                        log.error("Entered Here....."+filePart.filename());
                        fileName.set(filePart.filename());
                        contentType.set(Objects.requireNonNull(filePart.headers().getContentType()).toString());
                        return DataBufferUtils.join(filePart.content())
                                .map(dataBuffer -> {
                                    byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(fileBytes);
                                    return fileBytes;
                                });
                    })
                    .flatMap(fileBytes->{
                        log.error("Entered Here 3.....");
                       FileEntity fileEntity = new FileEntity();
                       fileEntity.setFileData(fileBytes);
                       fileEntity.setFilename(fileName.get());
                       fileEntity.setContentType(contentType.get());
                       return fileRepository.save(fileEntity);
                    });
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to upload file", e));
        }
        return Mono.just(new FileEntity());
    }

    @Override
    public Mono<FileEntity> getFileById(Long id) {
        return fileRepository.findById(id);
    }
}
