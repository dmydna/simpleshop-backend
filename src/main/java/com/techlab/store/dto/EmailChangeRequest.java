package com.techlab.store.dto;
import java.util.List;


public record EmailChangeRequest (
    String       password,
    String       newEmail
){}
