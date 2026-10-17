package com.application.reviewms.reviews.impl;

import com.application.reviewms.reviews.Review;
import com.application.reviewms.reviews.ReviewRepository;
import com.application.reviewms.reviews.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;

    }

    public List<Review> findAllReviews(Long companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }

    public boolean addReview(Long companyId, Review review){

        if(companyId != null && review != null){
            review.setCompanyId(companyId);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public boolean deleteReview(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
                reviewRepository.delete(review);
                return true;
            }
        return false;
    }

    public boolean updateReview(Long id, Review updatedReview){
        Review review = reviewRepository.findById(id).orElse(null);
        if(review != null){
            updatedReview.setId(id);
            review.setStar(updatedReview.getStar());
            review.setDescription(updatedReview.getDescription());
            review.setCompanyId(updatedReview.getCompanyId());
            reviewRepository.save(updatedReview);
            return true;
        }
        return false;
    }

}
