package com.prpo.chat.media.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.media.dto.MediaDto;
import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;
import com.prpo.chat.media.service.MediaService;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> getById(@PathVariable String id) {
        return mediaService.getById(id)
            .map(media -> ResponseEntity.ok(new MediaDto(media)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<MediaDto>> getByUploader(@PathVariable String uploaderId) {
        List<MediaDto> media = mediaService.getByUploaderId(uploaderId)
            .stream()
            .map(MediaDto::new)
            .toList();
        return ResponseEntity.ok(media);
    }

    @GetMapping("/type/{mediaType}")
    public ResponseEntity<List<MediaDto>> getByType(@PathVariable MediaType mediaType) {
        List<MediaDto> media = mediaService.getByMediaType(mediaType)
            .stream()
            .map(MediaDto::new)
            .toList();
        return ResponseEntity.ok(media);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MediaDto>> getAll() {
        List<MediaDto> media = mediaService.getAll()
            .stream()
            .map(MediaDto::new)
            .toList();
        return ResponseEntity.ok(media);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
