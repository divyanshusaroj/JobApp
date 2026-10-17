package com.application.reviewms.reviews;

import java.util.List;

public interface ReviewService {

    List<Review> findAllReviews(Long companyId);
    boolean addReview(Long companyId, Review review);
    Review getReviewById(Long id);
    boolean deleteReview(Long id);
    boolean updateReview(Long id, Review review);
}
