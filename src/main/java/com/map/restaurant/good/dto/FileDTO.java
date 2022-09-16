package com.map.restaurant.good.dto;

public class FileDTO {
    String fileId;
    String reviewId;
    Long fileSize;
    String fileName;
    String ContentType;
    String fileUpDateStr;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getFileUpDateStr() {
        return fileUpDateStr;
    }

    public void setFileUpDateStr(String fileUpDateStr) {
        this.fileUpDateStr = fileUpDateStr;
    }

}
