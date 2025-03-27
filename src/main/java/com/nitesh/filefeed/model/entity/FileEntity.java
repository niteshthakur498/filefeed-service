package com.nitesh.filefeed.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity {

    @Id
    private Long id;
    private String filename;
    private String contentType;
    private byte[] fileData;

}
