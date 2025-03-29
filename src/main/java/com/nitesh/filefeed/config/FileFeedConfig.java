package com.nitesh.filefeed.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "file-feed")
@Data
public class FileFeedConfig {
    private List<String> supportedFormats = new ArrayList<>();

    // Method to combine default and additional formats
    public List<String> getCombinedSupportedFormats() {
        // This will return a combined list of formats.
        return new ArrayList<>(supportedFormats);
    }


}
