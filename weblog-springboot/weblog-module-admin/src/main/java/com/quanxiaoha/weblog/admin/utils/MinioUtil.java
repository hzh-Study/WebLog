package com.quanxiaoha.weblog.admin.utils;

import com.quanxiaoha.weblog.admin.config.MinioProperties;
import com.quanxiaoha.weblog.common.exception.BizException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-05-11 9:02
 * @description: TODO
 **/
@Component
@Slf4j
public class MinioUtil {
    private static final String LOCAL_UPLOAD_DIR = "uploads";

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;

    public String uploadFile(MultipartFile file) throws Exception {
        if (file == null || file.getSize() == 0) {
            log.error("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        String key = UUID.randomUUID().toString().replace("-", "");
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        String objectName = String.format("%s%s", key, suffix);

        if (useLocalStorage()) {
            return uploadToLocal(file, objectName);
        }

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

        String url = String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), objectName);
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }

    private boolean useLocalStorage() {
        return isPlaceholder(minioProperties.getEndpoint())
                || isPlaceholder(minioProperties.getAccessKey())
                || isPlaceholder(minioProperties.getSecretKey())
                || isPlaceholder(minioProperties.getBucketName());
    }

    private boolean isPlaceholder(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String text = value.trim().toLowerCase();
        return text.contains("xxx");
    }

    private String uploadToLocal(MultipartFile file, String objectName) throws Exception {
        Path uploadDir = resolveLocalUploadDir();
        Files.createDirectories(uploadDir);
        Path targetFile = uploadDir.resolve(objectName);
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(objectName)
                .toUriString();
        log.info("==> 未配置有效 Minio，文件已落盘本地: {}, 访问路径: {}", targetFile, url);
        return url;
    }

    private Path resolveLocalUploadDir() {
        Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (currentDir.getFileName() != null && "weblog-web".equalsIgnoreCase(currentDir.getFileName().toString())) {
            return currentDir.getParent().resolve(LOCAL_UPLOAD_DIR).normalize();
        }
        return currentDir.resolve(LOCAL_UPLOAD_DIR).normalize();
    }
}
