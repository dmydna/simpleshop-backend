package com.techlab.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.techlab.store.entity.PendingReview;
import com.techlab.store.entity.User;

import java.util.Optional;

@Repository
public interface PendingReviewRepository extends JpaRepository<PendingReview, Long> {

    @Query("SELECT pr FROM PendingReview pr WHERE pr.user.id = :id")
    Page<PendingReview> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT pr FROM PendingReview pr WHERE pr.product.id = :id")
    Page<PendingReview> findAllByProductId(Long id, Pageable pageable);

    @Query("SELECT pr FROM PendingReview pr WHERE pr.user.id = :userId AND pr.id = :id")
    PendingReview findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT pr FROM PendingReview pr WHERE pr.user.id = :userId AND pr.id = :id")
    Page<PendingReview> findAllByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT pr FROM PendingReview pr WHERE pr.product.id = :productId AND pr.user.id = :userId")
    Page<PendingReview> findAllByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId ,Pageable pageable);


    Optional<PendingReview> findOneByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END FROM PendingReview pr WHERE pr.user.id = :userId AND pr.product.id = :productId")
    boolean existsByIdAndUserId(Long userId, Long productId);

}
