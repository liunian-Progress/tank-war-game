package com.tankwar.server.service;

import com.tankwar.server.model.ChatMessage;
import com.tankwar.server.model.Player;
import com.tankwar.server.model.PlayerStats;
import com.tankwar.server.repository.PlayerStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 聊天和排行榜服务
 */
@Service
public class ChatAndLeaderboardService {

    private final PlayerStatsRepository playerStatsRepository;
    private final Queue<ChatMessage> globalChatHistory = new ConcurrentLinkedQueue<>();
    private final Map<String, Queue<ChatMessage>> roomChatHistory = new HashMap<>();
    private static final int MAX_CHAT_HISTORY = 100;

    public ChatAndLeaderboardService(PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
    }

    /**
     * 发送聊天消息
     */
    public ChatMessage sendChatMessage(String playerId, String playerName, String content, String roomId) {
        // 过滤敏感词和限制长度
        content = filterContent(content);
        if (content.trim().isEmpty()) {
            return null;
        }

        ChatMessage message = new ChatMessage(playerId, playerName, content, "normal", roomId);
        
        if (roomId != null && !roomId.isEmpty()) {
            // 房间聊天
            roomChatHistory.computeIfAbsent(roomId, k -> new ConcurrentLinkedQueue<>()).add(message);
            // 限制历史记录数量
            Queue<ChatMessage> roomHistory = roomChatHistory.get(roomId);
            while (roomHistory.size() > MAX_CHAT_HISTORY) {
                roomHistory.poll();
            }
        } else {
            // 全局聊天
            globalChatHistory.add(message);
            // 限制历史记录数量
            while (globalChatHistory.size() > MAX_CHAT_HISTORY) {
                globalChatHistory.poll();
            }
        }

        return message;
    }

    /**
     * 发送系统消息
     */
    public ChatMessage sendSystemMessage(String content, String roomId) {
        ChatMessage message = new ChatMessage("system", "系统", content, "system", roomId);
        
        if (roomId != null && !roomId.isEmpty()) {
            roomChatHistory.computeIfAbsent(roomId, k -> new ConcurrentLinkedQueue<>()).add(message);
        } else {
            globalChatHistory.add(message);
        }

        return message;
    }

    /**
     * 获取聊天历史
     */
    public List<ChatMessage> getChatHistory(String roomId) {
        if (roomId != null && !roomId.isEmpty()) {
            Queue<ChatMessage> roomHistory = roomChatHistory.get(roomId);
            return roomHistory != null ? new ArrayList<>(roomHistory) : new ArrayList<>();
        } else {
            return new ArrayList<>(globalChatHistory);
        }
    }

    /**
     * 更新玩家统计数据
     */
    public void updatePlayerStats(Player player, boolean won, int playTime) {
        PlayerStats stats = playerStatsRepository.findByPlayerName(player.getName());
        
        if (stats == null) {
            stats = new PlayerStats(player.getName());
        }
        
        stats.updateStats(player, won, playTime);
        playerStatsRepository.save(stats);
    }

    /**
     * 获取总积分排行榜
     */
    public List<PlayerStats> getScoreLeaderboard() {
        return playerStatsRepository.findTop10ByTotalScore();
    }

    /**
     * 获取击杀排行榜
     */
    public List<PlayerStats> getKillsLeaderboard() {
        return playerStatsRepository.findTop10ByTotalKills();
    }

    /**
     * 获取胜率排行榜
     */
    public List<PlayerStats> getWinRateLeaderboard() {
        return playerStatsRepository.findTop10ByWinRate();
    }

    /**
     * 获取K/D比排行榜
     */
    public List<PlayerStats> getKillDeathRatioLeaderboard() {
        return playerStatsRepository.findTop10ByKillDeathRatio();
    }

    /**
     * 获取游戏场次排行榜
     */
    public List<PlayerStats> getGamesPlayedLeaderboard() {
        return playerStatsRepository.findTop10ByGamesPlayed();
    }

    /**
     * 获取总游戏时间排行榜
     */
    public List<PlayerStats> getPlayTimeLeaderboard() {
        return playerStatsRepository.findTop10ByTotalPlayTime();
    }

    /**
     * 获取玩家个人统计
     */
    public PlayerStats getPlayerStats(String playerName) {
        return playerStatsRepository.findByPlayerName(playerName);
    }

    /**
     * 获取所有排行榜数据
     */
    public Map<String, List<PlayerStats>> getAllLeaderboards() {
        Map<String, List<PlayerStats>> leaderboards = new HashMap<>();
        leaderboards.put("score", getScoreLeaderboard());
        leaderboards.put("kills", getKillsLeaderboard());
        leaderboards.put("winRate", getWinRateLeaderboard());
        leaderboards.put("killDeathRatio", getKillDeathRatioLeaderboard());
        leaderboards.put("gamesPlayed", getGamesPlayedLeaderboard());
        leaderboards.put("playTime", getPlayTimeLeaderboard());
        return leaderboards;
    }

    /**
     * 过滤聊天内容
     */
    private String filterContent(String content) {
        if (content == null) return "";
        
        // 限制长度
        if (content.length() > 100) {
            content = content.substring(0, 100);
        }
        
        // 简单的敏感词过滤（可以扩展）
        String[] sensitiveWords = {"垃圾", "傻逼", "fuck", "shit"};
        for (String word : sensitiveWords) {
            content = content.replaceAll("(?i)" + word, "***");
        }
        
        return content.trim();
    }

    /**
     * 清理过期聊天记录
     */
    public void cleanupChatHistory() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        
        // 清理全局聊天记录
        globalChatHistory.removeIf(msg -> msg.getTimestamp().isBefore(cutoff));
        
        // 清理房间聊天记录
        roomChatHistory.values().forEach(history -> 
            history.removeIf(msg -> msg.getTimestamp().isBefore(cutoff))
        );
    }

    /**
     * 获取聊天统计信息
     */
    public Map<String, Object> getChatStats() {
        return Map.of(
            "globalMessages", globalChatHistory.size(),
            "roomCount", roomChatHistory.size(),
            "totalMessages", globalChatHistory.size() + 
                roomChatHistory.values().stream().mapToInt(Queue::size).sum()
        );
    }
}
