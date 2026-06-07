package com.techlab.store.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.techlab.store.enums.UserStatus;

import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.User;
import com.techlab.store.mapper.ProfileMapper;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor 
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper; 

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
        @RequestPart("user") UserDTO user,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(userMapper.toEntity(user), file));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        UserDTO response = userMapper.toDto(user);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String clientname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserDTO> response = userService
           .filter(username, email, clientname, pageable)
           .map(user-> userMapper.toDto(user));
        return ResponseEntity.ok(response);
    }



    @PatchMapping("/{id}/unban-user")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/{id}/ban-user")
    public ResponseEntity<?> banUser(
        @PathVariable Long id, 
        @RequestBody BanRequest request
    ){
        userService.banUser(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        String url = userService.uploadImage(id, file);
        return ResponseEntity.ok(url);

    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long id){
        User user = userService.getById(id);
        return ResponseEntity.ok(profileMapper.toDto(user, user.getClient()));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<UserDTO> updateById(
        @PathVariable Long id, 
        @RequestBody UserDTO dataToEdit
    ){
        User user = userService
            .updateById(id, userMapper.toEntity(dataToEdit));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateStatus( 
        @PathVariable Long id, 
        @RequestParam UserStatus status) {
        User user = userService.updateStatusById(id, status);
        UserDTO response = userMapper.toDto(user);
        return ResponseEntity.ok(response);
    }


}
