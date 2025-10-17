package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天消息实体类
 */
public class ChatMessage {
    private String id;
    private String playerId;
    private String playerName;
    private String content;
    private String type; // normal, system, announcement
    private LocalDateTime timestamp;
    private String roomId;

    public ChatMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.type = "normal";
    }

    public ChatMessage(String playerId, String playerName, String content) {
        this();
        this.playerId = playerId;
        this.playerName = playerName;
        this.content = content;
    }

    public ChatMessage(String playerId, String playerName, String content, String type) {
        this(playerId, playerName, content);
        this.type = type;
    }

    public ChatMessage(String playerId, String playerName, String content, String type, String roomId) {
        this(playerId, playerName, content, type);
        this.roomId = roomId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
