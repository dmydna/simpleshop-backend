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

    // UserStatus -> String
    public static String userStatusToString(UserStatus status) {
        return status != null ? status.name() : null;
    }

    // String -> UserStatus
    public static UserStatus stringToUserStatus(String statusStr) {
        return statusStr != null ? UserStatus.valueOf(statusStr) : null;
    }


}