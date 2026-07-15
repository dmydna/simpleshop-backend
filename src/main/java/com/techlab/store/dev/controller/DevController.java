package com.techlab.store.dev.controller;

import java.util.List;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dev.service.AuthDevService;
import com.techlab.store.dev.service.ListingDevService;
import com.techlab.store.dev.service.ProductDevService;
import com.techlab.store.dev.service.UserDevService;
import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.CreateListingDTO;
import com.techlab.store.dto.ProductDTO;
import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.User;
import com.techlab.store.enums.ListingStatus;
import com.techlab.store.enums.Status;
import com.techlab.store.service.ListingService;
import com.techlab.store.service.ProductService;
import com.techlab.store.service.UserService;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.mapper.ProductMapper;
import com.techlab.store.mapper.ProfileMapper;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@Slf4j
@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevController {

    // LISTING
    private final ListingService listingService;
    private final ListingMapper listingMapper;
    private final ListingDevService listingDevService;
    // PRODUCT
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductDevService productDevService;
    // USER 
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserDevService userDevService;
    // PROFILE
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final AuthDevService authDevService;


    // Nota: 
    // Los endpoints: `/product/bulk`, `/auth/bulk` seran eliminados.
    // Usar endpoint `/listings/bulk`, que integra todas las funciones bulk (Todo en Uno). 
    @PostMapping("/listings/bulk")
    public ResponseEntity<List<ListingDTO>> createPosts(@RequestBody List<ListingDTO> listings) {
        // El service debe usar saveAll()
        log.info("iniciando listing bulk desde controller");
        List<ListingDTO> savedListings;
        savedListings = listingDevService.saveAll(listings);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedListings);
    }


    // @deprecated: este metodo no fue actualizado hace mucho y sera eliminado. 
    @PostMapping("/products/bulk")
    public ResponseEntity<List<Product>> createProducts(@RequestBody List<Product> products) {
        // El service debe usar saveAll()
        products.forEach(product -> product.setId(null));
        List<Product> savedProducts = productDevService.saveAll(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducts);
    }

    // @deprecated: este metodo no fue actualizado hace mucho y sera eliminado.  
    @PostMapping("/auth/bulk")
    public ResponseEntity<?> registerBulk(@RequestBody List<RegisterRequest> users) {
        List<User> savedUsers = authDevService.saveAll(users);
        return status(HttpStatus.CREATED)
                .body(savedUsers);
    }


    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> getAll(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String sku,
            @RequestParam(required = false, defaultValue = "") List<String> tags,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "ACTIVE") Status status,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity
                  .ok(productService
                    .filter(name, sku, tags, category, status, pageable)
                    .map(product -> productMapper.toDto(product)));
    }


    @GetMapping("/listings")
    public ResponseEntity<Page<ListingDTO>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(required = false) String availability,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
                .ok(listingService
                  .filter(title, category, tags, minPrice, maxPrice, status, availability,pageable)
                  .map(listing -> listingMapper.toDto(listing)));
    }



    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String clientname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // El Service decide si usa filtros o si devuelve todo
        return ResponseEntity
                 .ok(userService.filter(username, email, clientname, pageable)
                 .map(user -> userMapper.toDto(user)));
    }


    @GetMapping("/profiles")
    public ResponseEntity<Page<ProfileDTO>> getProfiles(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String clientname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // El Service decide si usa filtros o si devuelve todo
        return ResponseEntity
                 .ok(profileService.filter(username, email, clientname, pageable));
    }


}
