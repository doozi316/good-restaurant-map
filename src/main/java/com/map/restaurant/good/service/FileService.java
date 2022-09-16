package com.map.restaurant.good.service;

import com.map.restaurant.good.dao.FileDAO;
import com.map.restaurant.good.dto.FileDTO;
import com.map.restaurant.good.dto.ReviewDTO;
import com.map.restaurant.good.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    private Path imgDirPath;

    @Value("${file.storage.imgDir}")
    private String imgDir;

    @PostConstruct
    public void init() {
        this.imgDirPath = Paths.get(imgDir);
    }

    @Autowired
    private FileDAO fileDAO;

    public List<FileDTO> getImages(String reviewId) {
        return fileDAO.getImages(reviewId);
    }

    @Transactional
    public void deleteFiles(ReviewDTO reviewDTO) {
        List<String> fileIds = reviewDTO.getFileIds();
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        fileDAO.deleteFiles(fileIds);
    }

    @Transactional
    public void saveFiles(ReviewDTO reviewDTO) {
        List<MultipartFile> files = reviewDTO.getFiles();
        String reviewId = reviewDTO.getId();
        if (files == null || files.isEmpty()) {
            return;
        }

        try {
            if (! Files.exists(imgDirPath)) {
                Files.createDirectories(imgDirPath);
            }

            Path reviewImgDirPath = imgDirPath.resolve(
                Paths.get(reviewId)).normalize().toAbsolutePath();

            if (! Files.exists(reviewImgDirPath)) {
                Files.createDirectories(reviewImgDirPath);
            }
        } catch (IOException e) {
            throw new ApiException("Failed to make index dir", e);
        }

        for (MultipartFile file : files) {
            saveFile(file, reviewId);
        }
    }

    @Transactional
    public void saveFile(MultipartFile file, String reviewId) {
        try {
            String originFilename = file.getOriginalFilename();
            long fileSize = file.getSize();
            String contentType = file.getContentType();
            if (file.isEmpty() || originFilename == null) {
                throw new ApiException("Failed to store empty file.");
            }

            Path destinationFile = imgDirPath
                .resolve(Paths.get(reviewId))
                .resolve(Paths.get(originFilename))
                .normalize()
                .toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String fileId = UUID.randomUUID().toString();
            fileDAO.saveFile(fileId, reviewId, originFilename, fileSize, contentType);
        } catch (IOException e) {
            throw new ApiException("Failed to store file", e);
        }
    }
}
