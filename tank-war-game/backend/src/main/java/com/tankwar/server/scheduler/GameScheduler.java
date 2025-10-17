package com.tankwar.server.scheduler;

import com.tankwar.server.handler.TankWarWebSocketHandler;
import com.tankwar.server.service.GameService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 游戏定时任务调度器
 */
@Component
public class GameScheduler {

    private final GameService gameService;
    private final TankWarWebSocketHandler webSocketHandler;

    public GameScheduler(GameService gameService, TankWarWebSocketHandler webSocketHandler) {
        this.gameService = gameService;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * 每50毫秒更新一次游戏状态
     */
    @Scheduled(fixedRate = 50)
    public void updateGame() {
        gameService.updateGame();
    }

    /**
     * 每100毫秒广播一次游戏状态
     */
    @Scheduled(fixedRate = 100)
    public void broadcastGameState() {
        webSocketHandler.broadcastGameState();
    }

    /**
     * 每30秒生成新的道具
     */
    @Scheduled(fixedRate = 30000)
    public void generateNewPowerUps() {
        // 这里可以添加生成新道具的逻辑
        System.out.println("生成新道具...");
    }
}
