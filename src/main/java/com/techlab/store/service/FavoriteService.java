package com.techlab.store.service;

import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;
import com.techlab.store.entity.Favorite;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.FavoriteRepository;
import com.techlab.store.specification.FavoriteSpecifications;
import com.techlab.store.repository.UserRepository;
import com.techlab.store.utils.StringUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import com.techlab.store.dto.ListingShortDTO;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final ListingRepository listingRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;



    public Favorite create(Long listingId, Long userId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User no encontrado"));

        Favorite favorite = new Favorite();
        favorite.setListing(listing);
        favorite.setUser(user);
        favorite.setCreatedAt(LocalDateTime.now());
        
        return favoriteRepository.save(favorite);
    }


    public void delete(Long id, Long userId, boolean isAdmin) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite no encontrado"));

        // Si no es admin, verificar que pertenece al usuario
        if (!isAdmin && !favorite.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este favorite");
        }

        favoriteRepository.delete(favorite);
    }

    public Page<ListingShortDTO> filter(Long userId, Long id, Pageable pageable) {

        // Specification<Favorite> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        Specification<Favorite> spec = Specification
                .where(FavoriteSpecifications.hasUserId(userId))
                .and(FavoriteSpecifications.hasId(id));

        Page<Favorite> favoritePage = favoriteRepository.findAll(spec, pageable);
        return favoritePage.map(favorite -> toListingShortDto(favorite));
    }

    public ListingShortDTO toListingShortDto(Favorite favorite) {
        Listing listing = favorite.getListing();
        return new ListingShortDTO(
                favorite.getId(),
                favorite.getCreatedAt(),
                listing.getId(),
                listing.getThumbnail(),
                listing.getTitle(),
                listing.getPrice()
        );
    }

}
