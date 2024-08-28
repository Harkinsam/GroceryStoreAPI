package dev.sam.SpringRestApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AWSS3Service {
    private final S3AsyncClient s3AsyncClient;

    @Autowired
    public AWSS3Service(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }
    public CompletableFuture<String> uploadFile(MultipartFile file, String bucketName) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String contentType = getString(file, fileName);

        // Build the PutObjectRequest with the determined content type
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        try {
            return s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(file.getBytes()))
                    .thenApply(response -> "https://" + bucketName + ".s3.amazonaws.com/" + fileName)
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Failed to upload file to S3", throwable);
                    });
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file bytes",ex);
        }
    }

    private static String getString(MultipartFile file, String fileName) {
        String contentType = file.getContentType();

        // Fallback mechanism if content type is not recognized correctly
        if (contentType == null || contentType.equals("multipart/form-data")) {
            // Fallback to manually determining the content type based on the file extension
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.endsWith(".bmp")) {
                contentType = "image/bmp";
            } else {
                // If the file isn't a supported image type, let the controller handle the exception
                throw new IllegalArgumentException("Unsupported file type: " + fileName);
            }
        }
        return contentType;
    }

    public CompletableFuture<DeleteObjectResponse> deleteFile(String bucketName, String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // Return the CompletableFuture directly without logging or exception handling
        return s3AsyncClient.deleteObject(deleteObjectRequest);
    }

}
