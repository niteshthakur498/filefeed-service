package com.nitesh.filefeed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {

    private String id;

    private String contentType;

    private String url;

    private String fileName;

    private String externalReference;

}
