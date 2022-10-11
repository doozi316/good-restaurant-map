package com.map.restaurant.good.controller;

import com.map.restaurant.good.dao.ReviewDAO;
import com.map.restaurant.good.dto.ReviewDTO;
import com.map.restaurant.good.service.FileService;
import com.map.restaurant.good.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewCtrl {
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private FileService fileService;

    @PostMapping("/saveReview")
    public void saveReview(ReviewDTO reviewDTO) {
        reviewService.saveReview(reviewDTO);
        fileService.deleteFiles(reviewDTO);
        fileService.saveFiles(reviewDTO);
    }

    @GetMapping("/getReview")
    public ReviewDTO getReview(@RequestParam String reviewId) {
        return reviewService.getReview(reviewId);
    }

    @GetMapping("/getReviewsByKeySet")
    public List<ReviewDTO> getReviewsByKeySet(
        @RequestParam(value = "reviewUpdateDate", required = false) String reviewUpdateDate,
        @RequestParam(value = "reviewId", required = false) String reviewId) {
        return reviewService.getReviewsByKeySet(reviewUpdateDate, reviewId);
    }

    @GetMapping("/getReviewsForMap")
    public List<ReviewDTO> getReviewsForMap() {
        return reviewService.getReviewsForMap();
    }

    @DeleteMapping("/deleteReviews")
    public void deleteReviews(@RequestBody ReviewDTO reviewDTO) {
        reviewService.deleteReviews(reviewDTO);
    }
}
