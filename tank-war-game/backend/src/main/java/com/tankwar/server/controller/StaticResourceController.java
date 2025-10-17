package com.tankwar.server.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

/**
 * 静态资源控制器
 */
@Controller
public class StaticResourceController {

    @GetMapping("/")
    public ResponseEntity<Resource> index() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping("/game.js")
    public ResponseEntity<Resource> gameJs() throws IOException {
        Resource resource = new ClassPathResource("static/game.js");
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/javascript"))
                .body(resource);
    }

    // 避免与 WebSocket 端点 /tank-war 冲突：排除该路径
    @GetMapping("/{filename:^(?!tank-war$).+}")
    public ResponseEntity<Resource> staticFiles(@PathVariable String filename) throws IOException {
        Resource resource = new ClassPathResource("static/" + filename);
        if (resource.exists()) {
            MediaType mediaType = getMediaType(filename);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".html")) {
            return MediaType.TEXT_HTML;
        } else if (filename.endsWith(".js")) {
            return MediaType.valueOf("application/javascript");
        } else if (filename.endsWith(".css")) {
            return MediaType.valueOf("text/css");
        } else if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
