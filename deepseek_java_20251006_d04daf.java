package com.coursehost.controller;

import com.coursehost.model.Video;
import com.coursehost.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<byte[]> streamVideo(
            @PathVariable Long videoId,
            @RequestParam String token,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        
        try {
            // Verify access and token
            if (!videoService.canUserAccessVideo(user.getUsername(), videoId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Video video = videoService.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            if (!video.getStreamToken().equals(token) || video.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            byte[] videoBytes = videoService.getVideoBytes(videoId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(video.getMimeType()));
            headers.setContentLength(videoBytes.length);
            headers.set("Accept-Ranges", "bytes");
            headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.set("Pragma", "no-cache");
            headers.set("Expires", "0");
            
            // Prevent downloading
            headers.set("Content-Disposition", "inline");
            
            return new ResponseEntity<>(videoBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/token/{videoId}")
    public ResponseEntity<?> getStreamToken(@PathVariable Long videoId, 
                                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        try {
            if (!videoService.canUserAccessVideo(user.getUsername(), videoId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            String token = videoService.generateNewStreamToken(videoId);
            
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}