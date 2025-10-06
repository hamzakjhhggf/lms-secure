package com.coursehost.service;

import com.coursehost.model.Video;
import com.coursehost.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoService {

    @Value("${upload.video.path}")
    private String videoUploadPath;

    @Value("${upload.thumbnail.path}")
    private String thumbnailUploadPath;

    @Autowired
    private VideoRepository videoRepository;

    public Video saveVideo(Video video, MultipartFile file) throws IOException {
        // Create upload directories if they don't exist
        Files.createDirectories(Paths.get(videoUploadPath));
        Files.createDirectories(Paths.get(thumbnailUploadPath));

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file
        Path filePath = Paths.get(videoUploadPath, uniqueFilename);
        Files.write(filePath, file.getBytes());

        // Set video properties
        video.setFileName(uniqueFilename);
        video.setFilePath(filePath.toString());
        video.setFileSize(file.getSize());
        video.setMimeType(file.getContentType());
        video.setStreamToken(UUID.randomUUID().toString());
        video.setTokenExpiry(LocalDateTime.now().plusHours(2));

        return videoRepository.save(video);
    }

    public Optional<Video> findById(Long id) {
        return videoRepository.findById(id);
    }

    public boolean canUserAccessVideo(String userEmail, Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            return video.getModule().getCourse().getEnrolledUsers().stream()
                    .anyMatch(user -> user.getEmail().equals(userEmail));
        }
        return false;
    }

    public byte[] getVideoBytes(Long videoId) throws IOException {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            Path filePath = Paths.get(video.getFilePath());
            return Files.readAllBytes(filePath);
        }
        throw new IOException("Video not found");
    }

    public String generateNewStreamToken(Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setStreamToken(UUID.randomUUID().toString());
            video.setTokenExpiry(LocalDateTime.now().plusHours(2));
            videoRepository.save(video);
            return video.getStreamToken();
        }
        throw new RuntimeException("Video not found");
    }
}