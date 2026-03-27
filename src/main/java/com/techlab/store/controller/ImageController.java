package com.techlab.store.controller;


import com.techlab.store.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/image")
@Validated // Necesario para que funcionen las validaciones en los parámetros de URL
@RequiredArgsConstructor
public class ImageController {

    @Autowired
    private final ImageService imageService;

    // Límites máximos de seguridad (puedes ajustarlos)
    private static final long MAX_WIDTH = 4000;
    private static final long MAX_HEIGHT = 4000;
    private static final long MAX_AREA = MAX_WIDTH * MAX_HEIGHT; // Límite de área total (evita 4000x4000 combinados si es muy pesado)


    @GetMapping(value = "/{width}x{height}", produces = "image/svg+xml")
    public @ResponseBody byte[] generatePlaceholder(
            @PathVariable @Min(1) @Max(4000) int width,
            @PathVariable @Min(1) @Max(4000) int height,
            @RequestParam(defaultValue = "cccccc") String background, // Ahora es Param
            @RequestParam(defaultValue = "000000") String textColor,  // También lo pasamos a Param
            @RequestParam(defaultValue = "30%") String fontSize,
            @RequestParam(defaultValue = "thin") String fontWeight,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String icon
            ){

        if ((long) width * height > MAX_AREA) {
            throw new IllegalArgumentException("Image too large");
        }
        String finalLabel = (text != null && !text.isBlank()) ? text : width + "x" + height;
        return imageService
                .generateImage(
                        width,
                        height,
                        background,
                        textColor,
                        fontSize,
                        finalLabel,
                        fontWeight,
                        icon
                );
    }

}
