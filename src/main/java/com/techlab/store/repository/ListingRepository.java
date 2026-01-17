package com.techlab.store.repository;

import com.techlab.store.entity.Listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    @Query("SELECT l FROM Listing l WHERE l.id = :id AND l.deletedDate IS NULL")
    Optional<Listing> findActiveById(@Param("id") Long id);

    List<Listing> findAllByDeletedDateIsNull();
}
