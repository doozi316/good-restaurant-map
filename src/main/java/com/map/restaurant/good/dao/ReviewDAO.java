package com.map.restaurant.good.dao;

import com.map.restaurant.good.dto.ReviewDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface ReviewDAO {
    void saveReview(ReviewDTO reviewDTO);
    ReviewDTO getReview(@Param("reviewId") String reviewId);
    int getReviewsCnt();
    List<ReviewDTO> getReviewsByLimit(@Param("offset") Integer offset,
                                      @Param("countList") Integer countList);
    List<ReviewDTO> getReviewsForMap();
    void deleteReviews(@Param("reviewIds") List<String> reviewIds);
}
