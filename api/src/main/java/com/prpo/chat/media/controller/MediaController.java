package com.prpo.chat.media.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prpo.chat.media.dto.MediaDto;
import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.service.MediaService;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploaderId") String uploaderId,
            @RequestParam("mediaType") com.prpo.chat.media.entity.MediaType mediaType) throws IOException {
        
        Media media = mediaService.upload(
            uploaderId,
            file.getOriginalFilename(),
            file.getContentType(),
            mediaType,
            file.getSize(),
            file.getInputStream()
        );
        
        return ResponseEntity.ok(new MediaDto(media));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) throws IOException {
        GridFsResource resource = mediaService.download(id);
        
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(resource.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(new InputStreamResource(resource.getInputStream()));
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
    public ResponseEntity<List<MediaDto>> getByType(@PathVariable com.prpo.chat.media.entity.MediaType mediaType) {
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
