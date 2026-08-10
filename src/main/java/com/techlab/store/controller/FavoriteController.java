package com.techlab.store.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.techlab.store.entity.Favorite;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.FavoriteService;
import com.techlab.store.service.HashidService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthService authService;
    private final ListingMapper listingMapper;
    private final HashidService hashidService;

    @PostMapping("/{listingHash}")
    public ResponseEntity<?> create(@PathVariable String listingHash) {
        Long listingId = hashidService.decode(listingHash);
        User user = authService.getUser();
        Favorite response = favoriteService.create(listingId, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{listingHash}")
    public ResponseEntity<?> delete(@PathVariable String listingHash) {
        Long listingId = hashidService.decode(listingHash);
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        favoriteService.delete(listingId, user.getId(), isAdmin);
        Map<String,String> response = Map.of("message", "Favorito eliminado correctamente");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{listingHash}")
    public ResponseEntity<Favorite> getByListingId(@PathVariable String listingHash) {
        Long listingId = hashidService.decode(listingHash);
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        Favorite response = favoriteService.getByListingId(listingId, user.getId(), isAdmin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{listingHash}/check")
    public ResponseEntity<Map<String, Boolean>> isFavoriteListing(@PathVariable String listingHash) {
        Long listingId = hashidService.decode(listingHash);
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", favoriteService.isFavoriteListing(listingId, user.getId(), isAdmin));
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<ListingSummary>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long id,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        User user = authService.getUser();
        Page<Favorite> filtered = favoriteService.filter(user.getId(), id, pageable);

        return ResponseEntity.ok(filtered
            .map(favorite -> listingMapper.toSummaryDto(favorite.getListing())));
    }


}
