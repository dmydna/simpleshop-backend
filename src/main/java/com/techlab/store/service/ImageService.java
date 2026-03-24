package com.techlab.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


@Service
@RequiredArgsConstructor
public class ImageService {

    public byte[] createImage(
            int width, int height, String background, String textColor,
            String fontSize, String finalLabel, String fontFamily, String fontWeight) {
        try {
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // Configuración de colores (Soporta hex con o sin #)
            g2d.setColor(Color.decode(background.startsWith("#") ? background : "#" + background));
            g2d.fillRect(0, 0, width, height);

            // Suavizado de fuentes y bordes
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int weight = fontWeightCase(fontWeight);
            String family = fontFamily != null
                    && !fontFamily.trim().isEmpty() ? fontFamily : "SansSerif";

            // Configuración de fuente
            int size = Integer.parseInt(fontSize);
            g2d.setFont(new Font(family, weight, size));
            g2d.setColor(Color.decode(textColor.startsWith("#") ? textColor : "#" + textColor));

            // Centrado matemático
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(finalLabel)) / 2;
            int y = ((height - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(finalLabel, x, y);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error en el motor de renderizado: " + e.getMessage());
        }
    }

    public int fontWeightCase(String fontWeight){
        switch (fontWeight.toLowerCase()) {
            case "bold":
                return Font.BOLD;
            case "italic":
                return Font.ITALIC;
            case "bold_italic":
            case "bolditalic":
                return Font.BOLD | Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }




}
