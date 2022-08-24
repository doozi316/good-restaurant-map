package com.map.restaurant.good.dao;

import com.map.restaurant.good.dto.FileDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileDAO {
    List<FileDTO> getImages(@Param("reviewId") String reviewId);
    void saveFile(
        @Param("fileId") String fileId,
        @Param("reviewId") String reviewId,
        @Param("fileName") String fileName,
        @Param("fileSize") long fileSize,
        @Param("contentType") String contentType
    );
    void deleteFiles(@Param("fileIds") List<String> fileIds);
}
