package com.fiap.video_processor_producer.infrastructure.repository;

import java.io.IOException;
import java.nio.file.Path;

public interface S3Repository {
    void upload(String videoId, Path videoPath) throws IOException;
}
