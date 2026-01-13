package com.prpo.chat.media.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prpo.chat.media.entity.Media;
import com.prpo.chat.media.entity.MediaType;
import com.prpo.chat.media.repository.MediaRepository;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void upload_savesMediaAndUploadsToS3() {
        String uploaderId = "user-1";
        String filename = "test.png";
        String contentType = "image/png";
        MediaType mediaType = MediaType.IMAGE;
        long size = 1024L;
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());

        when(s3Service.uploadFile(any(InputStream.class), eq(filename), eq(contentType), eq(size)))
                .thenReturn("s3-key-123");
        when(mediaRepository.save(any(Media.class))).thenAnswer(inv -> {
            Media m = inv.getArgument(0);
            m.setId("media-1");
            return m;
        });

        Media result = mediaService.upload(uploaderId, filename, contentType, mediaType, size, inputStream);

        assertNotNull(result);
        assertEquals("s3-key-123", result.getS3Key());
        assertEquals(uploaderId, result.getUploaderId());
        assertEquals(filename, result.getFilename());

        verify(s3Service).uploadFile(any(InputStream.class), eq(filename), eq(contentType), eq(size));
        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void download_returnsInputStreamForExistingMedia() {
        String mediaId = "media-1";
        Media media = new Media("user-1", "test.png", "image/png", MediaType.IMAGE, 1024L);
        media.setS3Key("s3-key-123");

        S3Service.S3ObjectInputStream mockStream = new S3Service.S3ObjectInputStream(
                new ByteArrayInputStream("content".getBytes()), "image/png");

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));
        when(s3Service.downloadFile("s3-key-123")).thenReturn(mockStream);

        S3Service.S3ObjectInputStream result = mediaService.download(mediaId);

        assertNotNull(result);
        verify(s3Service).downloadFile("s3-key-123");
    }

    @Test
    void download_returnsNullForNonExistentMedia() {
        when(mediaRepository.findById("non-existent")).thenReturn(Optional.empty());

        S3Service.S3ObjectInputStream result = mediaService.download("non-existent");

        assertNull(result);
        verify(s3Service, never()).downloadFile(any());
    }

    @Test
    void getById_returnsOptionalMedia() {
        Media media = new Media("user-1", "test.png", "image/png", MediaType.IMAGE, 1024L);
        when(mediaRepository.findById("media-1")).thenReturn(Optional.of(media));

        Optional<Media> result = mediaService.getById("media-1");

        assertTrue(result.isPresent());
        assertEquals("test.png", result.get().getFilename());
    }

    @Test
    void getByUploaderId_returnsUserMedia() {
        Media media = new Media("user-1", "test.png", "image/png", MediaType.IMAGE, 1024L);
        when(mediaRepository.findByUploaderId("user-1")).thenReturn(List.of(media));

        List<Media> result = mediaService.getByUploaderId("user-1");

        assertEquals(1, result.size());
        assertEquals("user-1", result.get(0).getUploaderId());
    }

    @Test
    void delete_removesFromS3AndDatabase() {
        String mediaId = "media-1";
        Media media = new Media("user-1", "test.png", "image/png", MediaType.IMAGE, 1024L);
        media.setS3Key("s3-key-123");

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        mediaService.delete(mediaId);

        verify(s3Service).deleteFile("s3-key-123");
        verify(mediaRepository).deleteById(mediaId);
    }

    @Test
    void delete_handlesNonExistentMedia() {
        when(mediaRepository.findById("non-existent")).thenReturn(Optional.empty());

        mediaService.delete("non-existent");

        verify(s3Service, never()).deleteFile(any());
        verify(mediaRepository, never()).deleteById(any());
    }
}
