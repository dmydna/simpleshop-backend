package com.techlab.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.ListingDTO;
import com.techlab.store.service.StatisticsService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;


    @GetMapping("/topTags")
    public ResponseEntity< List<Map<String, Object>> > getPopularTags(
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(statisticsService.getPopularTags(limit));
    }


    @GetMapping("/topCategories")
    public ResponseEntity< List<Map<String, Object>> > getPopularCategories(
        @RequestParam(defaultValue="0", required = false) int limit){
        return ResponseEntity.ok(statisticsService.getPopularCategories(limit));
    }


    @GetMapping
    public ResponseEntity< Map<String, Object> > getGeneralStats(){
        return ResponseEntity.ok(statisticsService.getGeneralStats());
    }


}