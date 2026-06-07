package com.techlab.store.repository;

import java.util.List;

import com.techlab.store.entity.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface ReviewRepository extends JpaRepository<Review,Long>, JpaSpecificationExecutor<Review> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.user.username = :reviewerName")
    Optional<Review> findByProductIdAndReviewerName(@Param("productId") Long productId, @Param("reviewerName") String reviewerName);


    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    Optional<Review> findByUserId(@Param("userId") Long userId);


    @Query("SELECT r FROM Review r WHERE r.product.id = :productId")
    List<Review> findByProductId(@Param("productId") Long productId);


    @Query("SELECT pr FROM Review pr WHERE pr.product.id = :productId AND pr.user.id = :userId")
    Optional<Review> findOneByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);


}
