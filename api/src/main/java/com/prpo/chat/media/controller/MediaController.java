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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;

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

    @Operation(summary = "Upload a media file", description = "Uploads a file to S3 storage and saves metadata to the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully", content = @Content(schema = @Schema(implementation = MediaDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or parameters")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaDto> upload(
            @Parameter(description = "The file to upload", required = true) @RequestParam("file") MultipartFile file,

            @Parameter(description = "ID of the user uploading the file", required = true) @RequestParam("uploaderId") @NotBlank String uploaderId,

            @Parameter(description = "Type of media (IMAGE, VIDEO, AUDIO, DOCUMENT)", required = true) @RequestParam("mediaType") com.prpo.chat.media.entity.MediaType mediaType)
            throws IOException {

        Media media = mediaService.upload(
                uploaderId,
                file.getOriginalFilename(),
                file.getContentType(),
                mediaType,
                file.getSize(),
                file.getInputStream());

        String downloadUrl = s3Service.getPresignedUrl(media.getS3Key(), URL_EXPIRATION_MINUTES);
        return ResponseEntity.ok(new MediaDto(media, downloadUrl));
    }

    @Operation(summary = "Download a media file", description = "Downloads the actual file content from S3 storage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(
            @Parameter(description = "Media ID", required = true) @PathVariable @NotBlank String id)
            throws IOException {
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

    @Operation(summary = "Get media metadata by ID", description = "Returns the metadata for a single media item including a presigned download URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media found", content = @Content(schema = @Schema(implementation = MediaDto.class))),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> getById(
            @Parameter(description = "Media ID", required = true) @PathVariable @NotBlank String id) {
        return mediaService.getById(id)
                .map(media -> {
                    String downloadUrl = s3Service.getPresignedUrl(media.getS3Key(), URL_EXPIRATION_MINUTES);
                    return ResponseEntity.ok(new MediaDto(media, downloadUrl));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all media by uploader", description = "Returns all media items uploaded by a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media list retrieved successfully")
    })
    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<MediaDto>> getByUploader(
            @Parameter(description = "Uploader user ID", required = true) @PathVariable @NotBlank String uploaderId) {
        List<MediaDto> media = mediaService.getByUploaderId(uploaderId)
                .stream()
                .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
                .toList();
        return ResponseEntity.ok(media);
    }

    @Operation(summary = "Get all media by type", description = "Returns all media items of a specific type (IMAGE, VIDEO, AUDIO, DOCUMENT)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media list retrieved successfully")
    })
    @GetMapping("/type/{mediaType}")
    public ResponseEntity<List<MediaDto>> getByType(
            @Parameter(description = "Media type filter", required = true) @PathVariable com.prpo.chat.media.entity.MediaType mediaType) {
        List<MediaDto> media = mediaService.getByMediaType(mediaType)
                .stream()
                .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
                .toList();
        return ResponseEntity.ok(media);
    }

    @Operation(summary = "Get all media", description = "Returns all media items in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Media list retrieved successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<List<MediaDto>> getAll() {
        List<MediaDto> media = mediaService.getAll()
                .stream()
                .map(m -> new MediaDto(m, s3Service.getPresignedUrl(m.getS3Key(), URL_EXPIRATION_MINUTES)))
                .toList();
        return ResponseEntity.ok(media);
    }

    @Operation(summary = "Delete a media item", description = "Deletes the media metadata from the database and removes the file from S3")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Media deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Media not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Media ID", required = true) @PathVariable @NotBlank String id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
