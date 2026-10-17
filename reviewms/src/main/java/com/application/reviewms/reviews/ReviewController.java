package com.application.reviewms.reviews;

import com.application.reviewms.reviews.dto.ReviewMessage;
import com.application.reviewms.reviews.messaging.ReviewMessageProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/reviews")
@RestController
public class ReviewController {

    private ReviewService reviewService;
    private ReviewMessageProducer reviewMessageProducer;

    public ReviewController(ReviewService reviewService, ReviewMessageProducer reviewMessageProducer){
        this.reviewService = reviewService;
        this.reviewMessageProducer = reviewMessageProducer;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam Long companyId){
        return new ResponseEntity<>(reviewService.findAllReviews(companyId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestParam Long companyId, @RequestBody Review review){
        boolean added = reviewService.addReview(companyId,review);
        if(added) {
            reviewMessageProducer.sendMessage(review);
            return new ResponseEntity<>("Successfully added review", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No such company", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id){
        Review review = reviewService.getReviewById(id);
        if(review != null)
            return new ResponseEntity<>(review, HttpStatus.OK);
        return  new ResponseEntity<>(new Review(), HttpStatus.NOT_FOUND);
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
            boolean deleted = reviewService.deleteReview(id);
            if(deleted)
                return new ResponseEntity<>("Successfully deleted Review", HttpStatus.OK);
            return new ResponseEntity<>("No review with such Id", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReview(@PathVariable Long id, @RequestBody Review updatedReview){
        boolean updated = reviewService.updateReview(id, updatedReview);
        if(updated)
            return new ResponseEntity<>("Successfully updated review", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/averageRating")
    public Double averageCompanyRating(@RequestParam Long companyId){
        List<Review> reviewList = reviewService.findAllReviews(companyId);
        return reviewList.stream().mapToDouble(Review::getStar).average().orElse(0.0);
    }


}

