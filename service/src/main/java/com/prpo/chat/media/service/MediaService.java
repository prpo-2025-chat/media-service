package com.prpo.chat.media.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;
import com.prpo.chat.media.repository.MediaRepository;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final GridFsService gridFsService;

    public MediaService(MediaRepository mediaRepository, GridFsService gridFsService) {
        this.mediaRepository = mediaRepository;
        this.gridFsService = gridFsService;
    }

    public Media upload(String uploaderId, String filename, String contentType, 
                        MediaType mediaType, long size, InputStream inputStream) {
        String gridFsFileId = gridFsService.storeFile(inputStream, filename, contentType);
        
        Media media = new Media(uploaderId, filename, contentType, mediaType, size);
        media.setGridFsFileId(gridFsFileId);
        
        return mediaRepository.save(media);
    }

    public GridFsResource download(String mediaId) {
        Optional<Media> mediaOpt = mediaRepository.findById(mediaId);
        if (mediaOpt.isEmpty()) {
            return null;
        }
        
        Media media = mediaOpt.get();
        return gridFsService.getFile(media.getGridFsFileId());
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
            if (media.getGridFsFileId() != null) {
                gridFsService.deleteFile(media.getGridFsFileId());
            }
            mediaRepository.deleteById(id);
        }
    }
}
