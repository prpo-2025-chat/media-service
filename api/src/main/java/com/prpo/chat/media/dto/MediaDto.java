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
    private String s3Key;
    private Date uploadedAt;
    private String downloadUrl;

    public MediaDto() {}

    public MediaDto(Media media, String downloadUrl) {
        this.id = media.getId();
        this.uploaderId = media.getUploaderId();
        this.filename = media.getFilename();
        this.contentType = media.getContentType();
        this.mediaType = media.getMediaType();
        this.size = media.getSize();
        this.s3Key = media.getS3Key();
        this.uploadedAt = media.getUploadedAt();
        this.downloadUrl = downloadUrl;
    }

    public String getId() { return id; }
    public String getUploaderId() { return uploaderId; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public MediaType getMediaType() { return mediaType; }
    public long getSize() { return size; }
    public String getS3Key() { return s3Key; }
    public Date getUploadedAt() { return uploadedAt; }
    public String getDownloadUrl() { return downloadUrl; }

    public void setId(String id) { this.id = id; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public void setSize(long size) { this.size = size; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    public void setUploadedAt(Date uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}

