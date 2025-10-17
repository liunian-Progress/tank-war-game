package com.tankwar.server.model;

import java.util.Map;

/**
 * 游戏状态类
 */
public class GameState {
    private Map<String, Player> players;
    private java.util.List<Bullet> bullets;
    private java.util.List<Obstacle> obstacles;
    private java.util.List<PowerUp> powerUps;
    private boolean gameRunning;
    private long gameStartTime;
    private int mapWidth;
    private int mapHeight;

    public GameState() {
        this.gameRunning = true;
        this.gameStartTime = System.currentTimeMillis();
        this.mapWidth = 800;
        this.mapHeight = 600;
    }

    // Getters and Setters
    public Map<String, Player> getPlayers() { return players; }
    public void setPlayers(Map<String, Player> players) { this.players = players; }

    public java.util.List<Bullet> getBullets() { return bullets; }
    public void setBullets(java.util.List<Bullet> bullets) { this.bullets = bullets; }

    public java.util.List<Obstacle> getObstacles() { return obstacles; }
    public void setObstacles(java.util.List<Obstacle> obstacles) { this.obstacles = obstacles; }

    public java.util.List<PowerUp> getPowerUps() { return powerUps; }
    public void setPowerUps(java.util.List<PowerUp> powerUps) { this.powerUps = powerUps; }

    public boolean isGameRunning() { return gameRunning; }
    public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }

    public long getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }

    public int getMapWidth() { return mapWidth; }
    public void setMapWidth(int mapWidth) { this.mapWidth = mapWidth; }

    public int getMapHeight() { return mapHeight; }
    public void setMapHeight(int mapHeight) { this.mapHeight = mapHeight; }
}
