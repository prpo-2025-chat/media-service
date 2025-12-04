package com.prpo.chat.media.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;

public interface MediaRepository extends MongoRepository<Media, String> {
    
    List<Media> findByUploaderId(String uploaderId);
    
    List<Media> findByMediaType(MediaType mediaType);
}
