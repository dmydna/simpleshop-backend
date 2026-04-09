package com.techlab.store.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.entity.Listing;
import com.techlab.store.repository.ListingRepository;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.utils.StringUtils;

import org.springframework.data.jpa.domain.Specification;

import com.techlab.store.specification.ListingSpecifications;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class DevService {

    @Autowired
    private final ListingRepository listingRepository;
    private ListingMapper listingMapper;


    @Transactional
    public void saveAll(List<ListingDTO> dtos) {
    }




}