package com.techlab.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techlab.store.entity.Listing;
import com.techlab.store.enums.ReviewStatus;

import jakarta.transaction.Transactional;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    @Query("SELECT l FROM Listing l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Listing> findActiveById(@Param("id") Long id);

    Page<Listing> findAllByDeletedAtIsNull(Pageable pageable);

    // Filtra no eliminados.
    @Query("SELECT l FROM Listing l WHERE l.hash = :hash AND l.deletedAt IS NULL")
    Optional<Listing> findActiveByHash(@Param("hash") String hash);


    @Query("SELECT l FROM Listing l")
    Page<Listing> findAllSoft(Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.listingId = :listingId AND r.status = :status")
    void deleteReviewByListingIdAndStatus(
        @Param("listingId") Long listingId, 
        @Param("status") ReviewStatus status
    );


    // -- Filtrar (no eliminados) :

    // Por Title. (para busquedas)
    @Query("SELECT l FROM Listing l WHERE l.title LIKE %:keyword% AND l.deletedAt IS NULL")
    Page<Listing> searchByTitle(@Param("keyword") String keyword, Pageable pageable);


    List<Listing> findAllByDeletedAtIsNull();
}
