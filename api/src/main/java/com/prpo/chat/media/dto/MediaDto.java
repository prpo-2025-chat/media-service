package com.prpo.chat.media.dto;

import java.util.Date;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;

public class MediaDto {

    private String id;
    private String uploaderId;
    private String filename;
    private String contentType;
    private MediaType mediaType;
    private long size;
    private String storagePath;
    private Date uploadedAt;

    public MediaDto() {}

    public MediaDto(Media media) {
        this.id = media.getId();
        this.uploaderId = media.getUploaderId();
        this.filename = media.getFilename();
        this.contentType = media.getContentType();
        this.mediaType = media.getMediaType();
        this.size = media.getSize();
        this.storagePath = media.getStoragePath();
        this.uploadedAt = media.getUploadedAt();
    }

    public String getId() { return id; }
    public String getUploaderId() { return uploaderId; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public MediaType getMediaType() { return mediaType; }
    public long getSize() { return size; }
    public String getStoragePath() { return storagePath; }
    public Date getUploadedAt() { return uploadedAt; }

    public void setId(String id) { this.id = id; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public void setSize(long size) { this.size = size; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public void setUploadedAt(Date uploadedAt) { this.uploadedAt = uploadedAt; }
}
