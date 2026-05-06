package com.techlab.store.dev;


import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.OrderComplete;
import com.techlab.store.dto.UserDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.enums.Role;
import com.techlab.store.mapper.UserMapper;
import com.techlab.store.specification.ListingSpecifications;
import com.techlab.store.specification.UserSpecifications;
import com.techlab.store.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.techlab.store.entity.User;
import com.techlab.store.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import com.techlab.store.repository.UserRepository;
import com.techlab.store.service.FileStorageService;

@Slf4j
@Service
@RequiredArgsConstructor // <--- Genera el constructor automáticamente
public class UserDevService {


    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final StringUtils stringUtils;
    @Autowired
    private final FileStorageService fileStorageService;
    @Autowired
    private final UserMapper userMapper;

}