package com.deveshsharma.knowledgehub.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import com.deveshsharma.knowledgehub.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService implements FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(MinioClient minioClient, @Value("${minio.bucket.name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @PostConstruct
    void init() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }


    @Override
    public void uploadFile(MultipartFile file) throws Exception {
        uploadFile(file.getOriginalFilename(), file);
    }

    @Override
    public void uploadFile(String storageKey, MultipartFile file) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(storageKey)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    @Override
    public void deleteFile(String storageKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(storageKey)
                .build());
    }
}