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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.ListingShortDTO;
import com.techlab.store.entity.Favorite;
import com.techlab.store.entity.User;
import com.techlab.store.service.AuthService;
import com.techlab.store.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthService authService;


    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long listingId, @RequestBody Favorite favorite) {
        User user = authService.getUser();
        return ResponseEntity.ok(favoriteService.create(listingId, user.getId()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        User user = authService.getUser();
        boolean isAdmin = authService.isAdmin();
        favoriteService.delete(id, user.getId(), isAdmin);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<Page<ListingShortDTO>> getAll(        
        @RequestParam(required = false) Long userId, 
        @RequestParam(required = false) Long id,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        User user = authService.getUser();
        if (authService.isAdmin()) {
            return ResponseEntity.ok(favoriteService
                    .filter(userId, id, pageable));
        }
         return ResponseEntity.ok(favoriteService
                    .filter(user.getId(), id ,pageable));
    }
}