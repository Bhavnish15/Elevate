package com.project.Elevate.uploader_service.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCloudUploaderService implements UploaderService{

    private final Storage storage;

    @Value("${gcloud.storage-bucket-name}")
    private String googleStorageBucketName;

    @Override
    public String upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobInfo blobInfo = BlobInfo.newBuilder(googleStorageBucketName, fileName).build();
        try {
            storage.create(blobInfo, file.getBytes());
            return String.format("https://storage.googleapis.com/%s/%s", googleStorageBucketName, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}




//elevate-linkedin-clone