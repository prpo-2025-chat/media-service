package com.prpo.chat.media.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
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
import com.prpo.chat.media.service.S3Service;

@RestController
@RequestMapping("/media")
public class MediaController {

    private static final int URL_EXPIRATION_MINUTES = 60;

    private final MediaService mediaService;
    private final S3Service s3Service;

    public MediaController(MediaService mediaService, S3Service s3Service) {
        this.mediaService = mediaService;
        this.s3Service = s3Service;
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
        
        String downloadUrl = s3Service.getPresignedUrl(media.getS3Key(), URL_EXPIRATION_MINUTES);
        return ResponseEntity.ok(new MediaDto(media, downloadUrl));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) throws IOException {
        S3Service.S3ObjectInputStream s3Object = mediaService.download(id);
        
        if (s3Object == null) {
            return ResponseEntity.notFound().build();
        }
        
        Media media = mediaService.getById(id).orElse(null);
        String filename = media != null ? media.getFilename() : "download";
        
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(s3Object.getContentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(new InputStreamResource(s3Object.getInputStream()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> getById(@PathVariable String id) {
        return mediaService.getById(id)
            .map(media -> {
                String downloadUrl = s3Service.getPresignedUrl(media.getS3Key(), URL_EXPIRATION_MINUTES);
                return ResponseEntity.ok(new MediaDto(media, downloadUrl));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<MediaDto>> getByUploader(@PathVariable String uploaderId) {
        List<MediaDto> media = mediaService.getByUploaderId(uploaderId)
            .stream()
            .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
            .toList();
        return ResponseEntity.ok(media);
    }

    @GetMapping("/type/{mediaType}")
    public ResponseEntity<List<MediaDto>> getByType(@PathVariable com.prpo.chat.media.entity.MediaType mediaType) {
        List<MediaDto> media = mediaService.getByMediaType(mediaType)
            .stream()
            .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
            .toList();
        return ResponseEntity.ok(media);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MediaDto>> getAll() {
        List<MediaDto> media = mediaService.getAll()
            .stream()
            .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
            .toList();
        return ResponseEntity.ok(media);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

