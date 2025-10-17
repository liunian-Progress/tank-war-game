package com.tankwar.server.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tankwar.server.model.*;
import com.tankwar.server.repository.PlayerStatsRepository;
import com.tankwar.server.service.GameService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket处理器
 */
@Component
public class TankWarWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = Logger.getLogger(TankWarWebSocketHandler.class.getName());
    private final GameService gameService;
    private final PlayerStatsRepository playerStatsRepository;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public TankWarWebSocketHandler(GameService gameService, ObjectMapper objectMapper, PlayerStatsRepository playerStatsRepository) {
        this.gameService = gameService;
        this.objectMapper = objectMapper;
        this.playerStatsRepository = playerStatsRepository;
        // Ensure JavaTimeModule is registered in case auto-config not applied in websocket context
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("新连接建立: " + session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            TankWarMessage wsMessage = objectMapper.readValue(payload, TankWarMessage.class);
            handleClientMessage(session, wsMessage);
        }
    }

    private void handleClientMessage(WebSocketSession session, TankWarMessage message) {
        try {
            switch (message.getType()) {
                case "join":
                    handlePlayerJoin(session, message);
                    break;
                case "move":
                    handlePlayerMove(session, message);
                    break;
                case "shoot":
                    handlePlayerShoot(session, message);
                    break;
                case "chat":
                    handleChatMessage(session, message);
                    break;
                case "disconnect":
                    handlePlayerDisconnect(session);
                    break;
                case "respawn":
                    handlePlayerRespawn(session, message);
                    break;
            }
        } catch (Exception e) {
            System.err.println("处理客户端消息时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handlePlayerRespawn(WebSocketSession session, TankWarMessage message) {
        String playerId = message.getPlayerId();
        if (playerId == null) return;
        
        Player player = gameService.respawnPlayer(playerId);
        if (player != null) {
            System.out.println("玩家复活: " + player.getName() + " (ID: " + player.getId() + ")");
            
            // 向所有玩家广播游戏状态更新
            broadcastGameState();
            
            // 向复活的玩家发送确认消息
            TankWarMessage respawnConfirm = new TankWarMessage("respawnConfirmed", player.getId());
            respawnConfirm.setPlayerId(player.getId());
            sendMessage(session, respawnConfirm);
        }
    }

    private void handlePlayerJoin(WebSocketSession session, TankWarMessage message) {
        // 从data中获取playerName
        String playerName = null;
        if (message.getData() instanceof String) {
            playerName = (String) message.getData();
        } else if (message.getData() instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) message.getData();
            playerName = (String) data.get("playerName");
        }
        
        if (playerName == null || playerName.trim().isEmpty()) {
            System.err.println("无效的玩家名称");
            return;
        }
        
        Player player = gameService.addPlayer(playerName, session.getId());
        
        // 发送玩家ID（同时设置到 data 与 playerId 字段，兼容前端读取）
        TankWarMessage response = new TankWarMessage("playerId", player.getId());
        response.setPlayerId(player.getId());
        response.setPlayerName(player.getName());
        sendMessage(session, response);
        
        // 通知其他玩家（显式带上玩家名与ID，便于前端显示/更新列表）
        TankWarMessage joinNotification = new TankWarMessage("playerJoined", player.getName());
        joinNotification.setPlayerName(player.getName());
        joinNotification.setPlayerId(player.getId());
        broadcastMessage(joinNotification, session.getId());
        
        // 发送当前游戏状态
        sendGameState(session);
        
        System.out.println("玩家 " + playerName + " 加入游戏，ID: " + player.getId());
    }

    private void handlePlayerMove(WebSocketSession session, TankWarMessage message) {
        String playerId = message.getPlayerId();
        if (playerId == null) return;
        
        // 解析移动数据
        Map<String, Object> moveData = (Map<String, Object>) message.getData();
        if (moveData == null) return;
        
        double x = ((Number) moveData.get("x")).doubleValue();
        double y = ((Number) moveData.get("y")).doubleValue();
        int direction = ((Number) moveData.get("direction")).intValue();
        
        gameService.updatePlayerPosition(playerId, x, y, direction);
        
        // 广播位置更新
        TankWarMessage positionUpdate = new TankWarMessage("positionUpdate", moveData);
        positionUpdate.setPlayerId(playerId);
        broadcastMessage(positionUpdate);
    }

    private void handlePlayerShoot(WebSocketSession session, TankWarMessage message) {
        String playerId = message.getPlayerId();
        if (playerId == null) return;
        
        // 解析射击数据
        Map<String, Object> shootData = (Map<String, Object>) message.getData();
        if (shootData == null) return;
        
        double x = ((Number) shootData.get("x")).doubleValue();
        double y = ((Number) shootData.get("y")).doubleValue();
        int direction = ((Number) shootData.get("direction")).intValue();
        
        Bullet bullet = gameService.createBullet(playerId, x, y, direction);
        
        if (bullet != null) {
            // 广播子弹创建
            TankWarMessage bulletMessage = new TankWarMessage("bulletCreated", bullet);
            broadcastMessage(bulletMessage);
        }
    }

    private void handleChatMessage(WebSocketSession session, TankWarMessage message) {
        String playerId = message.getPlayerId();
        String playerName = message.getPlayerName();
        String text = message.getText();
        
        if (playerId != null && text != null && !text.trim().isEmpty()) {
            // 广播聊天消息
            TankWarMessage chatMessage = new TankWarMessage("chatMessage", playerId, playerName, text);
            broadcastMessage(chatMessage);
        }
    }

    private void handlePlayerDisconnect(WebSocketSession session) {
        String sessionId = session.getId();
        Player player = gameService.removePlayer(sessionId);
        
        if (player != null) {
            // 保存玩家分数到数据库
            savePlayerScoreToDatabase(player);
            
            // 通知其他玩家（显式带上玩家名与ID）
            TankWarMessage leaveNotification = new TankWarMessage("playerLeft", player.getName());
            leaveNotification.setPlayerName(player.getName());
            leaveNotification.setPlayerId(player.getId());
            broadcastMessage(leaveNotification, sessionId);
            
            System.out.println("玩家 " + player.getName() + " 离开游戏");
        }
        
        sessions.remove(sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket传输错误: " + exception.getMessage());
        handlePlayerDisconnect(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("连接关闭: " + session.getId() + ", 状态: " + closeStatus);
        String sessionId = session.getId();
        
        // 移除玩家
        Player player = gameService.removePlayer(sessionId);
        if (player != null) {
            logger.info("玩家离开游戏: " + player.getName());
            
            // 保存玩家分数到数据库
            savePlayerScoreToDatabase(player);
            
            // 通知其他玩家（显式带上玩家名与ID）
            TankWarMessage leaveNotification = new TankWarMessage("playerLeft", player.getName());
            leaveNotification.setPlayerName(player.getName());
            leaveNotification.setPlayerId(player.getId());
            broadcastMessage(leaveNotification, sessionId);
            
            sessions.remove(sessionId);
            // 更新游戏状态
            broadcastGameState();
        }
    }
    
    /**
     * 保存玩家分数到数据库
     */
    private void savePlayerScoreToDatabase(Player player) {
        try {
            PlayerStats stats = playerStatsRepository.findByPlayerName(player.getName());
            
            if (stats == null) {
                stats = new PlayerStats(player.getName());
            }
            
            // 更新数据库中的总分数为当前玩家的分数
            stats.setTotalScore(player.getScore());
            stats.setTotalKills(player.getKills());
            stats.setTotalDeaths(player.getDeaths());
            stats.setLastPlayTime(LocalDateTime.now());
            
            playerStatsRepository.save(stats);
            logger.info("保存玩家 " + player.getName() + " 的分数到数据库: " + player.getScore());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "保存玩家分数到数据库失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void sendMessage(WebSocketSession session, TankWarMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.err.println("发送消息失败: " + e.getMessage());
        }
    }

    private void broadcastMessage(TankWarMessage message) {
        broadcastMessage(message, null);
    }

    private void broadcastMessage(TankWarMessage message, String excludeSessionId) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            System.err.println("序列化消息失败: " + e.getMessage());
            return;
        }

        sessions.values().parallelStream()
                .filter(session -> !session.getId().equals(excludeSessionId))
                .filter(WebSocketSession::isOpen)
                .forEach(session -> {
                    try {
                        session.sendMessage(new TextMessage(json));
                    } catch (IOException e) {
                        System.err.println("广播消息失败: " + e.getMessage());
                    }
                });
    }

    private void sendGameState(WebSocketSession session) {
        GameState gameState = gameService.getGameState();
        TankWarMessage message = new TankWarMessage("gameState", gameState);
        sendMessage(session, message);
    }

    public void broadcastGameState() {
        GameState gameState = gameService.getGameState();
        TankWarMessage message = new TankWarMessage("gameState", gameState);
        broadcastMessage(message);
    }
}
