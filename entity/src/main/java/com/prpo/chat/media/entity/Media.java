package com.prpo.chat.media.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("media")
public class Media {

    @Id
    private String id;

    private String uploaderId;

    private String filename;

    private String contentType;

    private MediaType mediaType;

    private long size;


    private String gridFsFileId;

    @CreatedDate
    private Date uploadedAt;

    public Media() {}

    public Media(String uploaderId, String filename, String contentType, MediaType mediaType, long size) {
        this.uploaderId = uploaderId;
        this.filename = filename;
        this.contentType = contentType;
        this.mediaType = mediaType;
        this.size = size;
    }

    public String getId() { return id; }
    public String getUploaderId() { return uploaderId; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public MediaType getMediaType() { return mediaType; }
    public long getSize() { return size; }
    public String getGridFsFileId() { return gridFsFileId; }
    public Date getUploadedAt() { return uploadedAt; }

    public void setId(String id) { this.id = id; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public void setSize(long size) { this.size = size; }
    public void setGridFsFileId(String gridFsFileId) { this.gridFsFileId = gridFsFileId; }
    public void setUploadedAt(Date uploadedAt) { this.uploadedAt = uploadedAt; }
}
