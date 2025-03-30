package com.nitesh.filefeed.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonIgnore
    private String url;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("external_ref_no")
    private String externalReference;

}
