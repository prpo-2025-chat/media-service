package com.prpo.chat.media.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;
import com.prpo.chat.media.repository.MediaRepository;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;

    public MediaService(MediaRepository mediaRepository, S3Service s3Service) {
        this.mediaRepository = mediaRepository;
        this.s3Service = s3Service;
    }

    public Media upload(String uploaderId, String filename, String contentType, 
                        MediaType mediaType, long size, InputStream inputStream) {
        String s3Key = s3Service.uploadFile(inputStream, filename, contentType, size);
        
        Media media = new Media(uploaderId, filename, contentType, mediaType, size);
        media.setS3Key(s3Key);
        
        return mediaRepository.save(media);
    }

    public S3Service.S3ObjectInputStream download(String mediaId) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isEmpty()) {
            return null;
        }
        
        Media media = mediaOpt.get();
        return s3Service.downloadFile(media.getS3Key());
    }

    public Optional<Media> getById(String id) {
        return mediaRepository.findById(id);
    }

    public List<Media> getByUploaderId(String uploaderId) {
        return mediaRepository.findByUploaderId(uploaderId);
    }

    public List<Media> getByMediaType(MediaType mediaType) {
        return mediaRepository.findByMediaType(mediaType);
    }

    public List<Media> getAll() {
        return mediaRepository.findAll();
    }

    public void delete(String id) {
        Optional<Media> mediaOpt = mediaRepository.findById(id);
        if (mediaOpt.isPresent()) {
            Media media = mediaOpt.get();
            if (media.getS3Key() != null) {
                s3Service.deleteFile(media.getS3Key());
            }
            mediaRepository.deleteById(id);
        }
    }
}

