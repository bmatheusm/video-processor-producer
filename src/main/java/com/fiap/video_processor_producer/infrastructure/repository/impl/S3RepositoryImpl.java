package com.fiap.video_processor_producer.infrastructure.repository.impl;

import com.fiap.video_processor_producer.infrastructure.repository.S3Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3RepositoryImpl implements S3Repository {
    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    public S3RepositoryImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String videoId, Path videoPath) throws IOException {
        byte[] bytes = Files.readAllBytes(videoPath);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(videoId)
                .contentType("video/mp4")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
    }
}
