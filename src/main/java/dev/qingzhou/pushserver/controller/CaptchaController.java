package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.security.CaptchaService;
import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {

    private static final String[] PALETTE = {"#1f2937", "#0f766e", "#b45309", "#7c2d12"};

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping(value = {"/captcha"}, produces = "image/svg+xml")
    public ResponseEntity<String> captcha(HttpSession session) {
        String code = captchaService.generate(session);
        String svg = renderSvg(code);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(svg);
    }

    private String renderSvg(String code) {
        int width = 140;
        int height = 44;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder(512);
        builder.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
                .append(width)
                .append("\" height=\"")
                .append(height)
                .append("\" viewBox=\"0 0 ")
                .append(width)
                .append(" ")
                .append(height)
                .append("\">");
        builder.append("<rect width=\"100%\" height=\"100%\" fill=\"#f6f3ee\"/>");
        for (int i = 0; i < 5; i++) {
            builder.append("<line x1=\"")
                    .append(random.nextInt(width))
                    .append("\" y1=\"")
                    .append(random.nextInt(height))
                    .append("\" x2=\"")
                    .append(random.nextInt(width))
                    .append("\" y2=\"")
                    .append(random.nextInt(height))
                    .append("\" stroke=\"#cbd5e1\" stroke-width=\"1\"/>");
        }
        int spacing = (width - 20) / code.length();
        for (int i = 0; i < code.length(); i++) {
            char ch = code.charAt(i);
            int x = 10 + i * spacing + random.nextInt(-2, 3);
            int y = 30 + random.nextInt(-6, 7);
            int rotate = random.nextInt(-12, 13);
            String color = PALETTE[random.nextInt(PALETTE.length)];
            builder.append("<text x=\"")
                    .append(x)
                    .append("\" y=\"")
                    .append(y)
                    .append("\" fill=\"")
                    .append(color)
                    .append("\" font-family=\"'Segoe UI',sans-serif\" font-size=\"24\" font-weight=\"700\"")
                    .append(" transform=\"rotate(")
                    .append(rotate)
                    .append(" ")
                    .append(x)
                    .append(" ")
                    .append(y)
                    .append(")\">")
                    .append(ch)
                    .append("</text>");
        }
        builder.append("</svg>");
        return builder.toString();
    }
}
