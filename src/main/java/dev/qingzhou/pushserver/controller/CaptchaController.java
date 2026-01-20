package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.security.CaptchaService;
import jakarta.servlet.http.HttpSession;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping(value = {"/captcha"}, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> captcha(HttpSession session) {
        String code = captchaService.generate(session);
        byte[] png = renderPng(code);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    private byte[] renderPng(String code) {
        int width = 160;
        int height = 48;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // background
            g.setColor(new Color(245, 245, 245));
            g.fillRect(0, 0, width, height);
            // noise lines
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < 12; i++) {
                g.setColor(randomPastel(random));
                g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
            }
            // noise dots
            for (int i = 0; i < 120; i++) {
                g.setColor(randomPastel(random));
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                g.fillOval(x, y, 2, 2);
            }
            // characters
            int spacing = width / (code.length() + 2);
            for (int i = 0; i < code.length(); i++) {
                char ch = code.charAt(i);
                int x = (i + 1) * spacing + random.nextInt(-3, 4);
                int y = height / 2 + random.nextInt(10, 16);
                int fontSize = 26 + random.nextInt(-2, 3);
                g.setFont(new Font("Arial Black", Font.BOLD, fontSize));
                g.setColor(randomDeep(random));
                AffineTransform original = g.getTransform();
                double rotate = Math.toRadians(random.nextInt(-22, 23));
                double shearX = random.nextDouble(-0.25, 0.25);
                g.translate(x, y);
                g.rotate(rotate);
                g.shear(shearX, 0);
                g.drawString(String.valueOf(ch), 0, 0);
                g.setTransform(original);
            }
        } finally {
            g.dispose();
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (Exception ex) {
            // fallback: empty png
            return new byte[0];
        }
    }

    private Color randomPastel(ThreadLocalRandom random) {
        int r = 150 + random.nextInt(80);
        int g = 150 + random.nextInt(80);
        int b = 150 + random.nextInt(80);
        return new Color(r % 256, g % 256, b % 256);
    }

    private Color randomDeep(ThreadLocalRandom random) {
        int r = 30 + random.nextInt(120);
        int g = 30 + random.nextInt(120);
        int b = 30 + random.nextInt(120);
        return new Color(r, g, b);
    }
}
