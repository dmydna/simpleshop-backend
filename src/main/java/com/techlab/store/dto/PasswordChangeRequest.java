package com.techlab.store.dto;
import java.util.List;


public record PasswordChangeRequest (
    String       oldPassword,
    String       newPassword
){}
