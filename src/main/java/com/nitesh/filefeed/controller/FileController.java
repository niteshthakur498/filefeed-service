package com.nitesh.filefeed.controller;

import com.nitesh.filefeed.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileUploadService fileService;

    @PostMapping("/upload")
    public Mono<String> uploadFile(@RequestPart ("file")  Mono<FilePart> file) {
        log.error("ENtered here......................." +  file);
        file.doOnNext(fp->{
           if(fp==null){
               throw new RuntimeException("Failed..");
           }
        });
        return file
                .doOnNext(fp -> log.error("Received file -->"+fp.filename()))
                .then(Mono.just("file uploaded"));
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
