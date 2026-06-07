package com.techlab.store.dev.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.RegisterRequest;
import com.techlab.store.dto.ReviewDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.entity.Product;
import com.techlab.store.entity.Review;
import com.techlab.store.entity.User;
import com.techlab.store.enums.ReviewStatus;
import com.techlab.store.enums.Status;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.mapper.ReviewMapper;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.repository.ProductRepository;
import com.techlab.store.repository.ReviewRepository;
import com.techlab.store.repository.UserRepository;
import com.techlab.store.service.AuthService;
import com.techlab.store.utils.HashUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListingDevService {

    private final ListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final ListingMapper listingMapper;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public List<ListingDTO> saveAll(List<ListingDTO> dtos) {
        log.info("\n\n >> Iniciando persistencia masiva de {} elementos \n\n", dtos.size());

        if (dtos.isEmpty())
            return Collections.emptyList();

        // 1. Extraer datos únicos para carga masiva (Optimización N+1)
        Set<String> uniqueSkus = dtos.stream().map(ListingDTO::sku).collect(Collectors.toSet());
        Set<String> uniqueUsernames = dtos.stream()
                .flatMap(dto -> dto.reviews().stream())
                .map(ReviewDTO::username)
                .distinct()
                .collect(Collectors.toSet());

        // 2. Cargar entidades existentes en memoria (Map para búsqueda O(1))
        // Nota: Asegúrate de que findBySkuIn y findByUsernameIn existan en tus
        // Repositories
        Map<String, Product> existingProducts = new HashMap<>();
        if (!uniqueSkus.isEmpty()) {
            existingProducts = productRepository.findBySkuIn(uniqueSkus)
                    .stream()
                    .collect(Collectors.toMap(Product::getSku, p -> p));
        }

        Map<String, User> existingUsers = new HashMap<>();
        if (!uniqueUsernames.isEmpty()) {
            existingUsers = userRepository.findByUsernameIn(uniqueUsernames)
                    .stream()
                    .collect(Collectors.toMap(User::getUsername, u -> u));
        }

        // Estructura auxiliar para mantener la relación entre Listing y sus Reviews
        // pendientes
        class ListingReviewPair {
            Listing listing;
            List<Review> reviews;

            ListingReviewPair(Listing listing, List<Review> reviews) {
                this.listing = listing;
                this.reviews = reviews;
            }
        }

        List<ListingReviewPair> pairsToSave = new ArrayList<>();

        // 3. Construir objetos en memoria
        for (ListingDTO dto : dtos) {
            // --- Lógica de Producto ---
            Product product = existingProducts.get(dto.sku());
            if (product == null) {
                product = createNewProduct(dto, listingMapper.toEntity(dto));
                existingProducts.put(dto.sku(), product);
            }

            // --- Lógica de Usuario y Reviews (CORREGIDO) ---
            List<Review> reviews = new ArrayList<>();
            for (ReviewDTO rDto : dto.reviews()) {
                String username = rDto.username();
                User user = existingUsers.get(username);

                if (user == null) {
                    try {
                        // Intenta crear el usuario
                        user = registerByUsername(username);
                        // Si funciona, lo guardamos en el mapa para futuras iteraciones
                        existingUsers.put(username, user);
                    } catch (Exception e) {
                        // Intento de recuperación
                        log.warn("Error al crear usuario '{}': {}. Recuperando...", username, e.getMessage());

                        Optional<User> userOpt = userRepository.findByUsername(username);

                        if (userOpt.isPresent()) {
                            user = userOpt.get();
                            existingUsers.put(username, user);
                            log.info("✅ Usuario '{}' recuperado.", username);
                        } else {
                            // No se pudo crear ni encontrar. Saltamos esta review.
                            log.error("❌ No se pudo obtener usuario '{}'. La review asociada se omitirá.", username);
                            continue; // <--- SALTA ESTA REVIEW, NO ROMPE EL BUCLE
                        }

                    }
                }

                Review review = reviewMapper.toEntity(rDto);
                review.setProduct(product);
                review.setUser(user);
                review.setStatus(ReviewStatus.ACTIVE);
                // NO asignamos listing_id todavía
                reviews.add(review);
            }

            // --- Construir Listing ---
            Listing listing = listingMapper.toEntity(addDomianUrl(dto));
            listing.setProduct(product);
            listing.setHash(HashUtil.generateShortHash());
            listing.setCreatedAt(LocalDateTime.now());
            listing.setStatus(Status.ACTIVE);

            pairsToSave.add(new ListingReviewPair(listing, reviews));
        }

        // 4. Guardar todos los Listings (Aquí se generan los IDs)
        List<Listing> savedListings = listingRepository.saveAll(
                pairsToSave.stream()
                   .map(pair -> pair.listing)
                   .collect(Collectors.toList()));

        log.info("✅ {} Listings guardados con éxito.", savedListings.size());

        // 5. Asignar listing_id a las reviews y guardarlas
        List<Review> allReviewsToSave = new ArrayList<>();
        for (int i = 0; i < pairsToSave.size(); i++) {
            Listing savedListing = savedListings.get(i);
            List<Review> reviews = pairsToSave.get(i).reviews;

            // CORRECCIÓN: Asegúrate de usar el setter correcto (camelCase vs snake_case)
            // En Java estándar es setListingId, no setListing_id
            reviews.forEach(r -> r.setListingId(savedListing.getId()));

            allReviewsToSave.addAll(reviews);
        }

        if (!allReviewsToSave.isEmpty()) {
            reviewRepository.saveAll(allReviewsToSave);
            log.info("✅ {} Reviews guardadas con éxito.", allReviewsToSave.size());
        }


        // 6. Retornar DTOs actualizados
        return savedListings.stream()
                .map(listing -> {
                    // --- Actualizar Product Rating ---
                    updateProductRating(listing.getProduct());
                    return listingMapper.toDto(listing);
                })
                .collect(Collectors.toList());
    }

    // Helper para crear producto nuevo
    private Product createNewProduct(ListingDTO dto, Listing tempListing) {
        // Si tu entidad Product no tiene SKU por defecto, asegúrate de setearlo aquí
        // Asumiendo que el DTO tiene el SKU y el entity lo necesita
        // Nota: tempListing no se usa realmente aquí, pero lo dejo por si tu mapper lo
        // necesita
        Product newProduct = new Product();
        newProduct.setName(dto.productName());
        newProduct.setCategory(dto.category());
        newProduct.setTags(dto.tags());
        newProduct.setDimensions(dto.dimensions());
        newProduct.setBrand(dto.brand());
        newProduct.setWeight(dto.weight());
        newProduct.setSku(dto.sku());
        newProduct.setStatus(Status.ACTIVE);
        newProduct.setCreatedAt(LocalDateTime.now());
        // Copiar otros campos si es necesario desde el DTO o Listing
        return productRepository.save(newProduct);
    }

    public User registerByUsername(String username) {

        String mail = replaceUnderscoreWithDot(username) + "@gmail.com";
        String fname = capitalizeWords(username).get(0);
        String lname = capitalizeWords(username).get(1);
        String password = "password123";
        String image = "";
        String phone = "1111-2222";
        String address = "st_name 444";

        return authService.register(new RegisterRequest(
                username, password, image, mail, fname, lname, phone, address));
    }



    public ListingDTO addDomianUrl(ListingDTO dto){
        List<String> images = dto.images()
              .stream().map(url -> baseUrl + url)
              .collect(Collectors.toList());
        String thumbnail = baseUrl + dto.thumbnail();
        return new ListingDTO(
            dto.id(),
            dto.title(),
            dto.description(),
            dto.price(),
            dto.discountPercentage(),
            dto.rating(),
            dto.warrantyInformation(),
            dto.shippingInformation(),
            dto.availabilityStatus(),
            dto.reviews(),
            dto.returnPolicy(),
            dto.minimumOrderQuantity(),
            images,
            thumbnail,
            dto.hash(),
            // product
            dto.productId(),
            dto.productName(),
            dto.sku(),
            dto.brand(),
            dto.weight(),
            dto.dimensions(),
            dto.stock(),
            dto.category(),
            dto.tags(),
            dto.meta()
        );
    }

    public static String replaceUnderscoreWithDot(String input) {
        if (input == null)
            return null;
        return input.replace('_', '.');
    }

    public static List<String> capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return Arrays.asList(); // Retorna lista vacía si es null o vacío
        }

        return Arrays.stream(input.split("_"))
                .map(word -> {
                    if (word.isEmpty())
                        return "";
                    // Capitaliza la primera letra y deja el resto en minúsculas
                    return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                })
                .collect(Collectors.toList());
    }


    public Product updateProductRating(Product product){
        log.info("🔔 Actualizando Rating de producto ..." );
        Double rating = calcRating(product.getId());
        product.setRating(rating);
        return productRepository.save(product);
    }

    public Double calcRating(Long id){
        log.info("🔔 Calculando Rating de producto ..." );
        List<Review> reviews = reviewRepository.findByProductId(id);
        Double totalRating = 0.0;
        Integer totalReview = reviews.size();
        for(Review rev : reviews){
            totalRating += rev.getRating();
        }
        return (totalRating / totalReview);
    }


}
