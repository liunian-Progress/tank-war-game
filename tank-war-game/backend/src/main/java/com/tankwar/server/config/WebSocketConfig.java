package com.tankwar.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.tankwar.server.handler.TankWarWebSocketHandler;

/**
 * WebSocket配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TankWarWebSocketHandler tankWarWebSocketHandler;

    public WebSocketConfig(TankWarWebSocketHandler tankWarWebSocketHandler) {
        this.tankWarWebSocketHandler = tankWarWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tankWarWebSocketHandler, "/tank-war")
                .setAllowedOrigins("*"); // 允许跨域
    }
}
