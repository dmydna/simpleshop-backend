package com.techlab.store.repository;

import com.techlab.store.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByTitleContainingIgnoreCase(String name);

    List<Listing> findByCategoryContainingIgnoreCase(String category);

    List<Listing> findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category);
}
