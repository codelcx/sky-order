package com.sky.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
public class LocalStorageUtil {

    private final Path uploadDir;

    public LocalStorageUtil(Path uploadDir) {
        this.uploadDir = uploadDir;
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadDir, e);
        }
    }

    public String upload(MultipartFile file) {
        try {
            Files.createDirectories(uploadDir);

            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + suffix;
            Path targetPath = uploadDir.resolve(filename);
            file.transferTo(targetPath.toFile());

            String url = "/api/upload/" + filename;
            log.info("文件上传成功：{}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path targetPath = uploadDir.resolve(filename);
            Files.deleteIfExists(targetPath);
            log.info("文件删除成功：{}", filePath);
        } catch (IOException e) {
            log.error("文件删除失败：{}", filePath, e);
        }
    }

    public void deleteFileBatch(List<String> filePaths) {
        for (String filePath : filePaths) {
            deleteFile(filePath);
        }
    }
}
