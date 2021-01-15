package com.atguigu.crowd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload.path")
public class UploadProperties {
    private String serverFilesPath;

    public String getServerFilesPath() {
        return serverFilesPath;
    }

    public void setServerFilesPath(String serverFilesPath) {
        this.serverFilesPath = serverFilesPath;
    }
}
