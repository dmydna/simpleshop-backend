package com.techlab.store.utils;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class StringUtils {


  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
  );

  public static boolean isEmail(String email) {
    if (email == null || email.isEmpty()) {
        return false;
    }
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }

  public static String toLikePattern(String text){
    return "%" + text.toLowerCase() + "%";
  }

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
