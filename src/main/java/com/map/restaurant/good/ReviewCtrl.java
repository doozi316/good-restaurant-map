package com.map.restaurant.good;

import com.map.restaurant.good.dao.ReviewDAO;
import com.map.restaurant.good.dto.ReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
public class ReviewCtrl {
    @Autowired
    private ReviewDAO reviewDAO;

    @PostMapping("/saveReview")
    public void saveReview(@RequestBody ReviewDTO reviewDTO) {
        String id = reviewDTO.getId();
        if (id == null) {
            String uuidStr = UUID.randomUUID().toString();
            reviewDTO.setId(uuidStr);
        }
        reviewDAO.saveReview(reviewDTO);
    }

    @GetMapping("/getReviews")
    public List<ReviewDTO> getReviews() { return reviewDAO.getReviews(); }

    @DeleteMapping("/deleteReview")
    public void deleteReview(@RequestParam(value = "id", required = true) String id) {
        reviewDAO.deleteReview(id);
    }
}
