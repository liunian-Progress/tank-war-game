package com.tankwar.server.model;

/**
 * WebSocket消息类
 */
public class TankWarMessage {
    private String type;
    private Object data;
    private String playerId;
    private String playerName;
    private String text;

    public TankWarMessage() {}

    public TankWarMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public TankWarMessage(String type, String playerId, String playerName, String text) {
        this.type = type;
        this.playerId = playerId;
        this.playerName = playerName;
        this.text = text;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
