package com.example.geco.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.springframework.web.multipart.MultipartFile;

public final class ImageUtils {
    private ImageUtils() {}

    public static byte[] compressImage(MultipartFile file, float quality, int maxWidth, int maxHeight) throws IOException {
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null) throw new IOException("Invalid image file");

        int width = img.getWidth();
        int height = img.getHeight();

        double scale = 1.0;
        if (maxWidth > 0 && width > maxWidth) scale = Math.min(scale, (double) maxWidth / width);
        if (maxHeight > 0 && height > maxHeight) scale = Math.min(scale, (double) maxHeight / height);

        int newW = (int) Math.max(1, Math.round(width * scale));
        int newH = (int) Math.max(1, Math.round(height * scale));

        BufferedImage outImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outImg.createGraphics();
        g2d.drawImage(img, 0, 0, newW, newH, null);
        g2d.dispose();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) throw new IOException("No JPEG writer available");
        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            writer.setOutput(ios);
            writer.write(null, new IIOImage(outImg, null, null), param);
            ios.flush();
            return baos.toByteArray();
        } finally {
            writer.dispose();
        }
    }
}
