package com.techlab.store.repository;

import com.techlab.store.entity.Favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> ,JpaSpecificationExecutor<Favorite> {

    void deleteByIdAndUserId(Long id, Long userId);
    Optional<Favorite> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId); // solo funcionara si UserId es una campo, no una relacion (User).

    
    void deleteById(Long id); 

    @Query("SELECT f FROM Favorite f WHERE f.listing.id = :listingId AND f.user.id = :userId")
    Optional<Favorite> findByListingId(Long listingId, Long userId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.id = :id")
    Page<Favorite> findAllByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    Page<Favorite> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

}
