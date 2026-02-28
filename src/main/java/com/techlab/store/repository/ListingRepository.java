package com.techlab.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techlab.store.entity.Listing;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    @Query("SELECT l FROM Listing l WHERE l.id = :id AND l.deletedDate IS NULL")
    Optional<Listing> findActiveById(@Param("id") Long id);

    Page<Listing> findAllByDeletedDateIsNull(Pageable pageable);

    // Filtra no eliminados.
    @Query("SELECT l FROM Listing l WHERE l.hash = :hash AND l.deletedDate IS NULL")
    Optional<Listing> findActiveByHash(@Param("hash") String hash);



    // -- Filtrar (no eliminados) :

    // Por Title. (para busquedas)
    @Query("SELECT l FROM Listing l WHERE l.title LIKE %:keyword% AND l.deletedDate IS NULL")
    Page<Listing> searchByTitle(@Param("keyword") String keyword, Pageable pageable);


    List<Listing> findAllByDeletedDateIsNull();
}
