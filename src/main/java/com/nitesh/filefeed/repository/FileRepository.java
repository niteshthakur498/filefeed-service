package com.nitesh.filefeed.repository;

import com.nitesh.filefeed.model.entity.FileEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FileRepository extends ReactiveCrudRepository<FileEntity, Long> {
    Mono<FileEntity> findByFilename(String filename);
}
