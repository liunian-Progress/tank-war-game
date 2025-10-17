package com.tankwar.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 坦克大战游戏服务器主启动类
 * 
 * @author TankWar Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class TankWarServerApplication {

    public static void main(String[] args) {
        System.out.println("🚗 坦克大战服务器启动中...");
        SpringApplication.run(TankWarServerApplication.class, args);
        System.out.println("✅ 坦克大战服务器启动成功！");
        System.out.println("🌐 服务器地址: http://localhost:8080");
        System.out.println("🎮 WebSocket地址: ws://localhost:8080/tank-war");
    }
}
