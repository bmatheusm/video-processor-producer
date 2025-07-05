package com.fiap.video_processor_producer.usecase.upload;

import java.nio.file.Path;

public interface UploaderService {
    void uploadVideo(String videoId, Path videoPath);
}
