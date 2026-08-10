package com.techlab.store.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.BanRequest;
import com.techlab.store.dto.ProfileDTO;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.dto.UserSummary;
import com.techlab.store.entity.User;
import com.techlab.store.enums.UserStatus;
import com.techlab.store.mapper.ProfileMapper;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.service.HashidService;
import com.techlab.store.service.ProfileService;
import com.techlab.store.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// NOTA:
// - La division en dos entidades User/Client (Profile) es de uso interno (backend)
//   para frontend es indistinto y se devuelve como un unica entidad final (Usuario).
// - Usar UserService, ProfileService cuando corresponda.

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ProfileService profileService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final HashidService hashidService;

    // CREATE USER
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDTO> create(
            @RequestPart("user") UserDTO user,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        User createdUser = userService.create(userMapper.toEntity(user), file);
        ProfileDTO response = profileMapper.toDto(createdUser, createdUser.getClient());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET USER
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{hash}")
    public ResponseEntity<ProfileDTO> getById(@PathVariable String hash) {
        Long id = hashidService.decode(hash);
        User user = userService.getById(id);
        return ResponseEntity.ok(profileMapper.toDto(user, user.getClient()));
    }

    // UNBAN-USER
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hash}/unban-user")
    public ResponseEntity<?> unbanUser(@PathVariable String hash) {
        Long id = hashidService.decode(hash);
        userService.unbanUser(id);
        return ResponseEntity.ok().build();
    }

    // BAN-USER
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hash}/ban-user")
    public ResponseEntity<?> banUser(
            @PathVariable String hash,
            @RequestBody BanRequest request) {
        Long id = hashidService.decode(hash);
        userService.banUser(id, request);
        return ResponseEntity.ok().build();
    }

    // UPLOAD IMAGE
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String url = userService.uploadImage(id, file);
        return ResponseEntity.ok(url);

    }

    // UPDATE STATUS
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{hash}/status")
    public ResponseEntity<UserDTO> updateStatus(
            @PathVariable String hash,
            @RequestBody  Map<String, UserStatus> request) {
        Long id = hashidService.decode(hash);
        User user = userService.updateStatusById(id, request.get("status"));
        UserDTO response = userMapper.toDto(user);
        return ResponseEntity.ok(response);
    }


    // GET-ALL
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String clientname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDTO> response = userService
                .filter(username, email, clientname, pageable)
                .map(user -> userMapper.toDto(user));
        return ResponseEntity.ok(response);
    }

    /* --------------------------------------------------------- */

    // PROFILE (GET:ME)
    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMyProfile(authentication));
    }

    // PROFILE (UPDATE:ME)
    @PutMapping("/me")
    public ResponseEntity<ProfileDTO> updateProfile(
            Authentication authentication,
            @RequestBody ProfileDTO dataToEdit) {
        return ResponseEntity.ok(profileService
                .updateMyProfile(authentication, dataToEdit));
    }

    // PROFILE (UPLOAD-IMAGE:ME)
    @PutMapping(value = "me/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(profileService.updateProfileImage(authentication, file));
    }

    @GetMapping("/me/summary")
    public ResponseEntity<UserSummary>  getMeSummary(Authentication authentication){
        return ResponseEntity.ok(userService.getMeSummary(authentication));
    }

    // PROFILE (UPDATE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{hash}")
    public ResponseEntity<ProfileDTO> update(
            @PathVariable String hash, @RequestBody ProfileDTO dataToEdit) {
        Long id = hashidService.decode(hash);
        return ResponseEntity.ok(profileService
                .updateProfile(id, dataToEdit));
    }


}
