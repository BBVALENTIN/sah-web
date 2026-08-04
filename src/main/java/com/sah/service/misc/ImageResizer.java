package com.sah.service.misc;

import com.sah.FileUpload.FileResizerErrors;
import com.sah.FileUpload.FileResizerException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageResizer {

    private static final int MAX_SOURCE_DIMENSION = 8000;
    private static final long MAX_SOURCE_PIXELS = 40_000_000L;

    public static byte[] resizeToAvatar(byte[] originalByes, int targetSize) throws FileResizerException, IOException {

        BufferedImage original;

        try (ByteArrayInputStream in = new ByteArrayInputStream(originalByes)) {
            original = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(original == null) {
            throw new FileResizerException(FileResizerErrors.FILE_NOT_VALID);
        }

        int w = original.getWidth();
        int h = original.getHeight();

        if( w <= 0 || h <= 0 || w > MAX_SOURCE_DIMENSION || h > MAX_SOURCE_DIMENSION || (long) w * h > MAX_SOURCE_PIXELS) {
            throw new FileResizerException(FileResizerErrors.dimensionsError(w, h));
        }

        int cropSize = Math.min(w, h);

        int x = (w - cropSize) / 2;
        int y = (w - cropSize) / 2;

        BufferedImage resized = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resized.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(original, 0 , 0, targetSize, targetSize, x, y, x+cropSize, y+cropSize, null); //ugly - change
        } finally {
            g.dispose();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // jpegmafia
        if(!ImageIO.write(resized, "jpeg", out)) {
            throw new IOException(FileResizerErrors.NO_WRITER_AVAILABLE.toString());
        }

        return out.toByteArray();
    }
}
