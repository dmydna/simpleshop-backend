package com.techlab.store.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techlab.store.dto.FieldStats;
import com.techlab.store.dto.ListingSummary;
import com.techlab.store.enums.ListingFieldStats;
import com.techlab.store.enums.ProductFieldStats;
import com.techlab.store.enums.UserFieldStats;
import com.techlab.store.mapper.ListingMapper;
import com.techlab.store.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;
    private final ListingMapper listingMapper;


    @GetMapping("/listings/{field}")
    public ResponseEntity< List<FieldStats> > getListingFieldStats(
        @PathVariable ListingFieldStats field,
        @RequestParam(defaultValue="8", required = false) int limit)
    {
        String finalTable = field.equals(ListingFieldStats.tags) ? 
        "product_tags" : "listings";

        List<FieldStats> response = statisticsService
            .getStatsByField(finalTable, field.name(), limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{field}")
    public ResponseEntity< List<FieldStats> > getUserFieldStats(
        @PathVariable UserFieldStats field,
        @RequestParam(defaultValue="8", required = false) int limit){

        List<FieldStats> response = statisticsService
            .getStatsByField("users", field.name(), limit);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{field}")
    public ResponseEntity< List<FieldStats> > getProductFieldStats(
        @PathVariable ProductFieldStats field,
        @RequestParam(defaultValue="8", required = false) int limit){

       String finalTable = field.equals(ProductFieldStats.tags) ? 
        "product_tags" : "products";

        List<FieldStats> response = statisticsService
            .getStatsByField(finalTable, field.name(), limit);

        return ResponseEntity.ok(response);
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
