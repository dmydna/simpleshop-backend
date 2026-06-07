package com.techlab.store.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.ListingSummary;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.dto.FavoriteDTO;
import com.techlab.store.entity.Favorite;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthService authService;
    private final ListingMapper listingMapper;

    @PostMapping("/{listingId}")
    public ResponseEntity<?> create(@PathVariable Long listingId) {
        User user = authService.getUser();
        return ResponseEntity
                .ok(favoriteService.create(listingId, user.getId()));
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<?> delete(@PathVariable Long listingId) {
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        favoriteService.delete(listingId, user.getId(), isAdmin);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ListingSummary>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long id,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = authService.getUser();
        Long filterUserId = authService.isAdmin() 
          ? (userId != null ? userId : user.getId()) : user.getId();
        Page<Favorite> filtered = favoriteService.filter(filterUserId, id, pageable);

        return ResponseEntity.ok(filtered
            .map(favorite -> listingMapper.toSummaryDto(favorite.getListing())));
    }


}
