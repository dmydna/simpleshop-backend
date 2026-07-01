package com.techlab.store.utils;

import com.techlab.store.enums.*;

public class EnumUtils {
    

    // Status -> String
    public static String statusToString(Status status) {
        return status != null ? status.name() : null;
    }

    // String -> Status
    public static Status stringToStatus(String statusStr) {
        return statusStr != null ? Status.valueOf(statusStr) : null;
    }


    // Listing:  Status -> String
    public static String listingStatusToString(ListingStatus status) {
        return status != null ? status.name() : null;
    }


    // Listing:  String -> Status
    public static ListingStatus stringToListingStatus(String statusStr) {
        return statusStr != null ? ListingStatus.valueOf(statusStr) : null;
    }

    // User:  Status -> String
    public static String userStatusToString(UserStatus status) {
        return status != null ? status.name() : null;
    }

    // User:  String -> UserStatus
    public static UserStatus stringToUserStatus(String statusStr) {
        return statusStr != null ? UserStatus.valueOf(statusStr) : null;
    }


    public static boolean isStatusTransitionAllowed(
        ListingStatus current, ListingStatus target
    ) {
        /* Transicion de Status: 
           - DRAFT:    actualizable a (ACTIVE, DELETED)  
           - ACTIVE:   actualizable a (INACTIVE, DELETED) 
           - INACTIVE: actualizable a (ACTIVE, DELETED)  
           - DELETED:  no actualizable */

        if (current == ListingStatus.DELETED) 
           return false; // Ya validado arriba, pero por seguridad
    
        return switch (current) {
            case DRAFT    -> target == ListingStatus.ACTIVE || target == ListingStatus.DELETED;
            case ACTIVE   -> target == ListingStatus.INACTIVE || target == ListingStatus.DELETED;
            case INACTIVE -> target == ListingStatus.ACTIVE || target == ListingStatus.DELETED;
            case DELETED  -> false; // No debería llegar aquí
        };
    }


    public static boolean isStatusTransitionAllowed(
        UserStatus current, UserStatus target
    ){
        /* Transicion de Status: 
           - ACTIVE:   actualizable a (BANNED, DELETED) 
           - BANNED:   actualizable a (ACTIVE, DELETED)  
           - DELETED:  no actualizable */

        if (current == UserStatus.DELETED) 
           return false; // Ya validado arriba, pero por seguridad
    
        return switch (current) {
            case ACTIVE   -> target == UserStatus.BANNED || target == UserStatus.DELETED;
            case BANNED   -> target == UserStatus.ACTIVE || target == UserStatus.DELETED;
            case DELETED  -> false; // No debería llegar aquí
        };
    }


    public static boolean isStatusTransitionAllowed(
        Status current, Status target
    ){
        /* Transicion de Status: 
           - ACTIVE:   actualizable a (DELETED) 
           - DELETED:  no actualizable */

        if (current == Status.DELETED) 
           return false; // Ya validado arriba, pero por seguridad
    
        return switch (current) {
            case ACTIVE   -> target == Status.DELETED;
            case DELETED  -> false; // No debería llegar aquí
        };
    }


}