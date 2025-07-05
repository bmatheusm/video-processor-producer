package com.fiap.video_processor_producer.usecase.upload.impl;

import com.fiap.video_processor_producer.domain.exception.UploadException;
import com.fiap.video_processor_producer.infrastructure.repository.S3Repository;
import com.fiap.video_processor_producer.usecase.upload.UploaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    private S3Repository s3Repository;

    @Override
    public void uploadVideo(String videoId, Path videoPath) {
        try {
            s3Repository.upload(videoId, videoPath);

        } catch (IOException e) {
            log.error("Erro ao fazer upload do video: {}", e.getMessage());
            throw new UploadException(e.getMessage());
        }
    }
}
