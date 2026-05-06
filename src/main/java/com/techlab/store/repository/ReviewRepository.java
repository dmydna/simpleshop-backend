package com.techlab.store.repository;

import java.util.List;

import com.techlab.store.entity.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.user.username = :reviewerName")
    Optional<Review> findByProductIdAndReviewerName(@Param("productId") Long productId, @Param("reviewerName") String reviewerName);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId")
    List<Review> findByProductId(@Param("productId") Long productId);

}
