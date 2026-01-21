package com.example.geco.services;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

@Service
public class SupabaseStorageService implements StorageService {
    @Value("${SUPABASE_URL}")
    private String supabaseUrl;
    @Value("${SUPABASE_SERVICE_ROLE_KEY}")
    private String serviceKey;

    private final HttpClient http = HttpClient.newBuilder().build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String upload(MultipartFile file, String bucket, String key) throws IOException {
        String encodedBucket = URLEncoder.encode(bucket, StandardCharsets.UTF_8);
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String target = supabaseUrl + "/storage/v1/object/" + encodedBucket + "/" + encodedKey;

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofInputStream(() -> {
            try {
                return file.getInputStream();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(target))
            .header("Authorization", "Bearer " + serviceKey)
            .header("Content-Type", file.getContentType() == null ? "application/octet-stream" : file.getContentType())
            .PUT(body)
            .build();

        HttpResponse<String> resp;
        try {
            System.out.printf("Supabase upload -> base=%s bucket=%s key=%s contentType=%s fileName=%s\n", supabaseUrl, bucket, key, file.getContentType(), file.getOriginalFilename());
            resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Upload interrupted", e);
        }

        if (resp.statusCode() / 100 != 2) {
            System.err.printf("Supabase upload failed -> status=%d body=%s\n", resp.statusCode(), resp.body());
            throw new IOException("Supabase upload failed: " + resp.statusCode() + " " + resp.body());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + key;
    }

    @Override
    public String upload(InputStream input, String contentType, String bucket, String key) throws IOException {
        // Read all bytes from input then PUT to Supabase
        byte[] data;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = input.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
            data = baos.toByteArray();
        }

        String encodedBucket = URLEncoder.encode(bucket, StandardCharsets.UTF_8);
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String target = supabaseUrl + "/storage/v1/object/" + encodedBucket + "/" + encodedKey;

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(target))
            .header("Authorization", "Bearer " + serviceKey)
            .header("Content-Type", contentType == null ? "application/octet-stream" : contentType)
            .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
            .build();

        HttpResponse<String> resp;
        try {
            System.out.printf("Supabase upload -> base=%s bucket=%s key=%s contentType=%s size=%d\n", supabaseUrl, bucket, key, contentType, data.length);
            resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Upload interrupted", e);
        }

        if (resp.statusCode() / 100 != 2) {
            System.err.printf("Supabase upload failed -> status=%d body=%s\n", resp.statusCode(), resp.body());
            throw new IOException("Supabase upload failed: " + resp.statusCode() + " " + resp.body());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + key;
    }

    @Override
    public String getSignedUrl(String bucket, String key, int expiresInSeconds) throws IOException {
        String target = supabaseUrl + "/storage/v1/object/sign/"
            + URLEncoder.encode(bucket, StandardCharsets.UTF_8) + "/"
            + URLEncoder.encode(key, StandardCharsets.UTF_8) + "?expiresIn=" + expiresInSeconds;
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(target))
            .header("Authorization", "Bearer " + serviceKey)
            .GET()
            .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) throw new IOException("Sign URL failed: " + resp.body());
            JsonNode node = mapper.readTree(resp.body());
            return node.get("signedURL").asText();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Sign URL interrupted", e);
        }
    }
    
    @Override
    public void delete(String bucket, String key) throws IOException {
        String target = supabaseUrl + "/storage/v1/object/" +
            URLEncoder.encode(bucket, StandardCharsets.UTF_8) + "/" +
            URLEncoder.encode(key, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(target))
            .header("Authorization", "Bearer " + serviceKey)
            .DELETE()
            .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new IOException("Supabase delete failed: " + resp.statusCode() + " " + resp.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Delete interrupted", e);
        }
    }
}