package com.example.dongyucar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3AsyncUploader {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Async("uploadExecutor")
    public CompletableFuture<String> uploadAsync(
            InputStream inputStream,
            String fileName,
            long size,
            String contentType
    ) {
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
}
