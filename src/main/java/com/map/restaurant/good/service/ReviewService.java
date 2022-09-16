package com.map.restaurant.good.service;

import com.map.restaurant.good.dao.ReviewDAO;
import com.map.restaurant.good.dto.ReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    @Autowired
    private ReviewDAO reviewDAO;

    @Transactional
    public void saveReview(ReviewDTO reviewDTO) {
        String id = reviewDTO.getId();
        if (id == null || id.isEmpty()) {
            String uuidStr = UUID.randomUUID().toString();
            reviewDTO.setId(uuidStr);
        }
        reviewDAO.saveReview(reviewDTO);
    }

    public List<ReviewDTO> getReviews() {
        return reviewDAO.getReviews();
    }

    @Transactional
    public void deleteReviews(ReviewDTO reviewDTO) {
        List<String> reviewIds = reviewDTO.getReviewIds();
        reviewDAO.deleteReviews(reviewIds);
    }
}
