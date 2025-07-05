package com.fiap.video_processor_producer.infrastructure.controller;

import com.fiap.video_processor_producer.usecase.upload.UploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping
public class VideoController {

    @Autowired
    private UploaderService uploaderService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("video") MultipartFile videoFile) {

        String originalName = videoFile.getOriginalFilename();
        if (originalName == null || !isValidVideoFile(originalName)) {
            return ResponseEntity.badRequest().body("Formato de arquivo inválido. Use: mp4, avi, mov, mkv");
        }

        try {
            String id = UUID.randomUUID().toString();
            Path tempFile = Files.createTempFile(id, ".mp4");
            videoFile.transferTo(tempFile.toFile());
            uploaderService.uploadVideo(id, tempFile);
            Files.deleteIfExists(tempFile);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro no upload do video" + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadZip(@PathVariable String filename) throws IOException {
        Path filePath = outputsDir.resolve(filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> status() throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> filesList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputsDir, "*.zip")) {
            for (Path file : stream) {
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("filename", file.getFileName().toString());
                fileInfo.put("size", Files.size(file));
                fileInfo.put("created_at", Files.getLastModifiedTime(file).toString());
                fileInfo.put("download_url", "/download/" + file.getFileName());
                filesList.add(fileInfo);
            }
        }

        response.put("files", filesList);
        response.put("total", filesList.size());
        return ResponseEntity.ok(response);
    }

    private boolean isValidVideoFile(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return List.of(".mp4", ".avi", ".mov", ".mkv", ".wmv", ".flv", ".webm").contains(ext);
    }
}
