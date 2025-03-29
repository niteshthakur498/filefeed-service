package com.nitesh.filefeed.utils;

import com.nitesh.filefeed.config.FileFeedConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileFormatValidator {

    private final FileFeedConfig fileFormatConfig;



    /**
     * Validates the file format based on its extension.
     *
     * @param filename The name of the file to validate.
     * @return true if the file format is supported, false otherwise.
     */
    public boolean isValidFormat(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        return fileFormatConfig.getCombinedSupportedFormats().contains(extension);
    }
}
