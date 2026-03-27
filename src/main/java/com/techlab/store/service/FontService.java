package com.techlab.store.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FontService {

    @Value("${app.font.default:inter.ttf}")
    private String defaultFont;

    private Map<String, String> fontsCache = new HashMap<>();
    private String defaultFontBase64;

    @PostConstruct
    public void init() {
        this.defaultFontBase64 = loadFont(defaultFont);
    }

    public String getFontBase64() {
        return defaultFontBase64;
    }

    public String getFontBase64(String fontFile) {
        if (fontFile == null || fontFile.isBlank()) {
            return defaultFontBase64;
        }

        if (fontsCache.containsKey(fontFile)) {
            return fontsCache.get(fontFile);
        }

        String fontBase64 = loadFont(fontFile);
        fontsCache.put(fontFile, fontBase64);
        return fontBase64;
    }

    private String loadFont(String fontFile) {
        try {
            Resource resource = new ClassPathResource("static/fonts/" + fontFile);
            byte[] fontBytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return Base64.getEncoder().encodeToString(fontBytes);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando fuente: " + fontFile, e);
        }
    }
}