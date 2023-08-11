package com.example.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ResourceNotFoundException;
import com.example.models.Review;
import com.example.repositorys.MovieRepository;
import com.example.repositorys.ReviewRepository;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
public class ReviewController {

    private ReviewRepository reviewRepository;
    private MovieRepository movieRepository;

    public ReviewController(ReviewRepository reviewRepository, MovieRepository movieRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<Review>> getAllReviewsByImdbId(@PathVariable Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("movieId " + movieId + " not found"));
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable Long movieId, @Valid @RequestBody Review reviewRequest) {
        Review review = movieRepository.findById(movieId).map(movie -> {
            reviewRequest.setMovie(movie);
            return reviewRepository.save(reviewRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("movieId " + movieId + " not found"));
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @PutMapping("/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long movieId, @PathVariable Long reviewId,
            @Valid @RequestBody Review reviewRequest) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("movieId " + movieId + " not found");
        }

        Review newReview = reviewRepository.findById(reviewId).map(review -> {
            review.setBody(reviewRequest.getBody());
            return reviewRepository.save(review);
        }).orElseThrow(() -> new ResourceNotFoundException("reviewId " + reviewId + " not found"));

        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @DeleteMapping("/movies/{movieId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("movieId " + movieId + " not found");
        }
        reviewRepository.deleteById(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
