package com.map.restaurant.good.dao;

import com.map.restaurant.good.dto.ReviewDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface ReviewDAO {
    void saveReview(ReviewDTO reviewDTO);
    ReviewDTO getReview(@Param("reviewId") String reviewId);
    List<ReviewDTO> getReviewsByKeySet(@Param("reviewUpdateDate") String reviewUpdateDate,
                                      @Param("reviewId") String reviewId);
    List<ReviewDTO> getReviewsForMap();
    void deleteReviews(@Param("reviewIds") List<String> reviewIds);
}
