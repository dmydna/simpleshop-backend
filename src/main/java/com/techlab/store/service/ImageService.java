package com.techlab.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;


import java.util.*;
import java.lang.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Autowired
    private FontService fontService;

    @Value("${app.font.file:inter.ttf}")
    private String fontFile;



    public byte[] generateImage( int width, int height, String background, String textColor,
                                 String fontSize, String finalLabel, String fontWeight, String icon) {

        boolean isIconFont = icon != null && !icon.isBlank();
        // Si es icono, el contenido del text es el unicode del icono
        // Si es texto, el contenido es el label
        String content = isIconFont ? parseIconUnicode(icon) : finalLabel;
        // Si es icono, usar la fuente de iconos, si no, usar la fuente del parámetro
        String font = isIconFont ? "bootstrap-icons.woff2" : "inter.ttf" ;
        return generateSVG(width, height, background, textColor, fontSize, content, font, fontWeight, isIconFont);
    }

    public byte[] generateSVG(
            int width, int height, String background, String textColor,
            String fontSize, String finalLabel, String fontFamily, String fontWeight,
            boolean isIconFont) {

        try {
            String bgColor = background.startsWith("#") ? background : "#" + background;
            String textColorHex = textColor.startsWith("#") ? textColor : "#" + textColor;

            String fontWeightValue = isIconFont ? "normal" : mapFontWeight(fontWeight);
            String fontBase64 = fontService.getFontBase64(fontFamily);

            // Detectar formato de fuente
            String fontFormat = detectFontFormat(fontFamily);

            String svg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d">
                <defs>
                    <style>
                        @font-face {
                            font-family: 'custom-font';
                            src: url('data:font/%s;base64,%s') format('%s');
                        }
                    </style>
                </defs>
                <rect width="100%%" height="100%%" fill="%s"/>
                <text 
                    x="50%%" 
                    y="50%%" 
                    font-family="custom-font"
                    font-size="%spx"
                    font-weight="%s"
                    text-anchor="middle" 
                    dominant-baseline="middle"
                    fill="%s">
                    %s
                </text>
            </svg>
            """.formatted(width, height, fontFormat, fontBase64, fontFormat, bgColor, fontSize, fontWeightValue, textColorHex, finalLabel);

            return svg.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error generando SVG: " + e.getMessage());
        }
    }

    private String detectFontFormat(String fontFamily) {
        if (fontFamily.endsWith(".woff2")) {
            return "woff2";
        } else if (fontFamily.endsWith(".woff")) {
            return "woff";
        } else if (fontFamily.endsWith(".otf")) {
            return "opentype";
        } else {
            return "truetype"; // Default para .ttf
        }
    }

    private String mapFontWeight(String fontWeight) {
        return switch (fontWeight.toLowerCase()) {
            case "thin" -> "100";
            case "extra_light" -> "200";
            case "light" -> "300";
            case "normal" -> "400";
            case "medium" -> "500";
            case "semi_bold" -> "600";
            case "bold" -> "700";
            case "extra_bold" -> "800";
            case "black" -> "900";
            default -> "400";
        };
    }


    private String parseIconUnicode(String icon) {
        if (icon == null || icon.isBlank()) {
            return icon;
        }

        // Si viene como U+F4E1 o u+f4e1
        if (icon.startsWith("U+") || icon.startsWith("u+")) {
            try {
                int codePoint = Integer.parseInt(icon.substring(2), 16);
                return new String(Character.toChars(codePoint));
            } catch (NumberFormatException e) {
                return icon; // Devuelve original si falla
            }
        }

        // Si viene como F4E1 (solo el código hexadecimal)
        if (icon.matches("^[0-9A-Fa-f]{3,6}$")) {
            try {
                int codePoint = Integer.parseInt(icon, 16);
                return new String(Character.toChars(codePoint));
            } catch (NumberFormatException e) {
                return icon;
            }
        }

        // Si viene como &#xF4E1; dejarlo como está
        if (icon.startsWith("&#x")) {
            return icon;
        }

        // Si es un carácter directo, devolverlo tal cual
        return icon;
    }

}
