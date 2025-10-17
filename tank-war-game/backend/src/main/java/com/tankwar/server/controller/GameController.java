package com.tankwar.server.controller;

import com.tankwar.server.model.Player;
import com.tankwar.server.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * 获取排行榜
     */
    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard() {
        return gameService.getLeaderboard();
    }

    /**
     * 获取在线玩家数量
     */
    @GetMapping("/players/count")
    public int getPlayerCount() {
        return gameService.getPlayerCount();
    }

    /**
     * 检查游戏是否已满
     */
    @GetMapping("/game/full")
    public boolean isGameFull() {
        return gameService.isGameFull();
    }

    /**
     * 获取游戏状态
     */
    @GetMapping("/game/state")
    public Object getGameState() {
        return gameService.getGameState();
    }
}
