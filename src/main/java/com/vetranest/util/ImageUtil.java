package com.vetranest.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Utility for loading and ultra-sharp bicubic scaling of raster and icon assets.
 */
public class ImageUtil {

    /**
     * Loads an image from classpath resources and scales it with high-quality Bicubic antialiasing.
     * Prevents blurriness when downscaling high-resolution assets.
     */
    public static ImageIcon loadScaledIcon(String resourcePath, int targetWidth, int targetHeight) {
        try (InputStream in = ImageUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.err.println("[ImageUtil] Asset not found: " + resourcePath);
                return null;
            }
            BufferedImage original = ImageIO.read(in);
            if (original == null) return null;

            // Multi-step progressive bilinear/bicubic scaling for pin-sharp downsampling
            BufferedImage scaled = scaleImageSharp(original, targetWidth, targetHeight);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("[ImageUtil] Error loading image " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Produces ultra-sharp scaled BufferedImage using progressive downscaling with Bicubic interpolation.
     */
    public static BufferedImage scaleImageSharp(BufferedImage img, int targetW, int targetH) {
        int w = img.getWidth();
        int h = img.getHeight();

        // Progressive halving if shrinking by more than 2x to maintain crystal-clear sharpness
        while (w > targetW * 2 && h > targetH * 2) {
            w /= 2;
            h /= 2;
            BufferedImage step = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = step.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            img = step;
        }

        // Final target pass with Bicubic interpolation and antialiasing
        BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.drawImage(img, 0, 0, targetW, targetH, null);
        g2.dispose();

        return result;
    }

    /**
     * Loads raw BufferedImage from classpath resource.
     */
    public static BufferedImage loadImage(String resourcePath) {
        try (InputStream in = ImageUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) return null;
            return ImageIO.read(in);
        } catch (Exception e) {
            System.err.println("[ImageUtil] Error reading " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }
}
