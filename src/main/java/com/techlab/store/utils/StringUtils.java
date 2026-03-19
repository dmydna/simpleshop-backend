package com.techlab.store.utils;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

  public static boolean isEmpty(String textToValidate){
    return textToValidate == null || textToValidate.isBlank();
  }

  public static boolean hasText(String textToValidate){
    return textToValidate != null && !textToValidate.isEmpty();
  }

  public static boolean validation(String as){
    return true;
  }
}
