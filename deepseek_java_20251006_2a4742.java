package com.coursehost.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private Integer duration; // in seconds
    private String streamToken;
    private LocalDateTime tokenExpiry;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        streamToken = java.util.UUID.randomUUID().toString();
        tokenExpiry = LocalDateTime.now().plusHours(2);
    }

    // Constructors, Getters, and Setters
    public Video() {}

    public Video(String title, String fileName, String filePath, Module module) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.module = module;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getStreamToken() { return streamToken; }
    public void setStreamToken(String streamToken) { this.streamToken = streamToken; }
    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}