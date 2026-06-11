package com.techlab.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.entity.Listing;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.service.StatisticsService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;
    private final ListingMapper listingMapper;



    @GetMapping("/top/tags")
    public ResponseEntity< List<Map<String, Object>> > getPopularTags(
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(statisticsService.getPopularTags(limit));
    }


    @GetMapping("/top/categories")
    public ResponseEntity< List<Map<String, Object>> > getPopularCategories(
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(statisticsService.getPopularCategories(limit));
    }


    @GetMapping("/top/sales")
    public ResponseEntity< List<ListingSummary> > getTopSales (
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(
            statisticsService
                .getTopSales(limit)
                .stream()
                .map(l->listingMapper.toSummaryDto(l) )
                .collect(Collectors.toList() )

        );
    }


    @GetMapping("/top/visits")
    public ResponseEntity< List<ListingSummary> > getTopVisit (
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(
            statisticsService
                .getTopVisit(limit)
                .stream()
                .map(l->listingMapper.toSummaryDto(l) )
                .collect(Collectors.toList() )

        );
    }


    @GetMapping("/top/rated")
    public ResponseEntity< List<ListingSummary> > getTopRated (
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(
            statisticsService
                .getTopRated(limit)
                .stream()
                .map(l->listingMapper.toSummaryDto(l) )
                .collect(Collectors.toList() )
        );
    }

    @GetMapping("/top/onsale")
    public ResponseEntity< List<ListingSummary> > getTopOnsale (
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(
            statisticsService
                .getTopOnsale(limit)
                .stream()
                .map(l->listingMapper.toSummaryDto(l) )
                .collect(Collectors.toList() )
        );
    }



    @GetMapping
    public ResponseEntity< Map<String, Object> > getGeneralStats(){
        return ResponseEntity.ok(statisticsService.getGeneralStats());
    }


}