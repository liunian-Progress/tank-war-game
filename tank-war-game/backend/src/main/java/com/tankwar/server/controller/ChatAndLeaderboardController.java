package com.tankwar.server.controller;

import com.tankwar.server.model.ChatMessage;
import com.tankwar.server.model.PlayerStats;
import com.tankwar.server.service.ChatAndLeaderboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聊天和排行榜API控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatAndLeaderboardController {

    private final ChatAndLeaderboardService chatAndLeaderboardService;

    public ChatAndLeaderboardController(ChatAndLeaderboardService chatAndLeaderboardService) {
        this.chatAndLeaderboardService = chatAndLeaderboardService;
    }

    /**
     * 发送聊天消息
     */
    @PostMapping("/chat/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> request) {
        String playerId = request.get("playerId");
        String playerName = request.get("playerName");
        String content = request.get("content");
        String roomId = request.get("roomId");

        ChatMessage message = chatAndLeaderboardService.sendChatMessage(playerId, playerName, content, roomId);
        
        if (message != null) {
            return Map.of("success", true, "message", message);
        } else {
            return Map.of("success", false, "message", "消息发送失败");
        }
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/chat/history")
    public List<ChatMessage> getChatHistory(@RequestParam(required = false) String roomId) {
        return chatAndLeaderboardService.getChatHistory(roomId);
    }

    /**
     * 获取总积分排行榜
     */
    @GetMapping("/leaderboard/score")
    public List<PlayerStats> getScoreLeaderboard() {
        return chatAndLeaderboardService.getScoreLeaderboard();
    }

    /**
     * 获取击杀排行榜
     */
    @GetMapping("/leaderboard/kills")
    public List<PlayerStats> getKillsLeaderboard() {
        return chatAndLeaderboardService.getKillsLeaderboard();
    }

    /**
     * 获取胜率排行榜
     */
    @GetMapping("/leaderboard/winrate")
    public List<PlayerStats> getWinRateLeaderboard() {
        return chatAndLeaderboardService.getWinRateLeaderboard();
    }

    /**
     * 获取K/D比排行榜
     */
    @GetMapping("/leaderboard/kd")
    public List<PlayerStats> getKillDeathRatioLeaderboard() {
        return chatAndLeaderboardService.getKillDeathRatioLeaderboard();
    }

    /**
     * 获取游戏场次排行榜
     */
    @GetMapping("/leaderboard/games")
    public List<PlayerStats> getGamesPlayedLeaderboard() {
        return chatAndLeaderboardService.getGamesPlayedLeaderboard();
    }

    /**
     * 获取总游戏时间排行榜
     */
    @GetMapping("/leaderboard/playtime")
    public List<PlayerStats> getPlayTimeLeaderboard() {
        return chatAndLeaderboardService.getPlayTimeLeaderboard();
    }

    /**
     * 获取所有排行榜
     */
    @GetMapping("/leaderboard/all")
    public Map<String, List<PlayerStats>> getAllLeaderboards() {
        return chatAndLeaderboardService.getAllLeaderboards();
    }

    /**
     * 获取玩家个人统计
     */
    @GetMapping("/player/{playerName}/stats")
    public PlayerStats getPlayerStats(@PathVariable String playerName) {
        return chatAndLeaderboardService.getPlayerStats(playerName);
    }

    /**
     * 获取聊天统计信息
     */
    @GetMapping("/chat/stats")
    public Map<String, Object> getChatStats() {
        return chatAndLeaderboardService.getChatStats();
    }
}
