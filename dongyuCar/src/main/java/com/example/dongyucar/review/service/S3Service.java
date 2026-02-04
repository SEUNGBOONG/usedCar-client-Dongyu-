package com.example.dongyucar.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // 동기 업로드 (ReviewService 백그라운드 스레드에서 사용)
    public String uploadFile(InputStream inputStream, String fileName, long size, String contentType) {

        String key = "reviews/" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(inputStream, size));

        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    // 비동기 업로드 (VehicleService에서 사용)
    @Async
    public CompletableFuture<String> uploadAsync(InputStream inputStream, String fileName, long size, String contentType) {

        String key = "vehicle/" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromInputStream(inputStream, size));

        return CompletableFuture.completedFuture(
                "https://" + bucket + ".s3.amazonaws.com/" + key
        );
    }

    public void deleteFile(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(req);
    }
}
