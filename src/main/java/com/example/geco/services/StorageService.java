package com.example.geco.services;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile file, String bucket, String key) throws IOException;
    String upload(InputStream input, String contentType, String bucket, String key) throws IOException;
    String getSignedUrl(String bucket, String key, int expiresInSeconds) throws IOException;
    void delete(String bucket, String key) throws IOException;
}
