package com.techlab.store.repository;

import com.techlab.store.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.reviewerName = :reviewerName")
    Optional<Review> findByProductIdAndReviewerName(@Param("productId") Long productId, @Param("reviewerName") String reviewerName);
}
