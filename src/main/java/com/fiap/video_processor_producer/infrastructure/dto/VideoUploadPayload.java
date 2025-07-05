package com.fiap.video_processor_producer.infrastructure.dto;

import lombok.Getter;

@Getter
public class VideoUploadPayload {
    private String videoId;
    private String videoFileName;
}
