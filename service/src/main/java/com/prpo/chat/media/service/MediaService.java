package com.prpo.chat.media.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;
import com.prpo.chat.media.repository.MediaRepository;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media save(Media media) {
        return mediaRepository.save(media);
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
        mediaRepository.deleteById(id);
    }
}
