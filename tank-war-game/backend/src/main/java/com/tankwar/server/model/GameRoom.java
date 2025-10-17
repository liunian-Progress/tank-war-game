package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 游戏房间实体类
 */
public class GameRoom {
    private String id;
    private String name;
    private int maxPlayers;
    private int currentPlayers;
    private String status; // waiting, playing, finished
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private ConcurrentHashMap<String, Player> players;
    private List<Obstacle> obstacles;
    private List<PowerUp> powerUps;
    private List<Bullet> bullets;
    private String mapType;
    private int mapWidth;
    private int mapHeight;

    public GameRoom() {
        this.id = UUID.randomUUID().toString();
        this.maxPlayers = 8;
        this.currentPlayers = 0;
        this.status = "waiting";
        this.createTime = LocalDateTime.now();
        this.players = new ConcurrentHashMap<>();
        this.obstacles = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.mapType = "classic";
        this.mapWidth = 800;
        this.mapHeight = 600;
    }

    public GameRoom(String name, int maxPlayers) {
        this();
        this.name = name;
        this.maxPlayers = maxPlayers;
    }

    public boolean addPlayer(Player player) {
        if (currentPlayers >= maxPlayers || !"waiting".equals(status)) {
            return false;
        }
        
        players.put(player.getId(), player);
        currentPlayers++;
        
        // 如果房间满了，开始游戏
        if (currentPlayers >= maxPlayers) {
            startGame();
        }
        
        return true;
    }

    public Player removePlayer(String playerId) {
        Player player = players.remove(playerId);
        if (player != null) {
            currentPlayers--;
            
            // 如果房间空了，重置状态
            if (currentPlayers == 0) {
                status = "waiting";
            }
        }
        return player;
    }

    public void startGame() {
        this.status = "playing";
        this.startTime = LocalDateTime.now();
        generateMap();
    }

    public void endGame() {
        this.status = "finished";
    }

    private void generateMap() {
        // 生成地图障碍物和道具
        generateObstacles();
        generatePowerUps();
    }

    private void generateObstacles() {
        // 生成经典地图布局
        obstacles.clear();
        
        // 边界墙
        obstacles.add(new Obstacle(0, 0, mapWidth, 20, "wall")); // 上边界
        obstacles.add(new Obstacle(0, mapHeight - 20, mapWidth, 20, "wall")); // 下边界
        obstacles.add(new Obstacle(0, 0, 20, mapHeight, "wall")); // 左边界
        obstacles.add(new Obstacle(mapWidth - 20, 0, 20, mapHeight, "wall")); // 右边界
        
        // 中央障碍物
        obstacles.add(new Obstacle(300, 200, 200, 20, "brick"));
        obstacles.add(new Obstacle(300, 220, 20, 160, "brick"));
        obstacles.add(new Obstacle(480, 220, 20, 160, "brick"));
        obstacles.add(new Obstacle(300, 380, 200, 20, "brick"));
        
        // 随机障碍物
        for (int i = 0; i < 10; i++) {
            double x = 50 + Math.random() * (mapWidth - 100);
            double y = 50 + Math.random() * (mapHeight - 100);
            double width = 30 + Math.random() * 20;
            double height = 30 + Math.random() * 20;
            
            String[] types = {"wall", "brick", "steel"};
            String type = types[(int) (Math.random() * types.length)];
            
            obstacles.add(new Obstacle(x, y, width, height, type));
        }
    }

    private void generatePowerUps() {
        powerUps.clear();
        String[] types = {"speed", "damage", "health", "shield"};
        
        for (int i = 0; i < 5; i++) {
            double x = 50 + Math.random() * (mapWidth - 100);
            double y = 50 + Math.random() * (mapHeight - 100);
            String type = types[(int) (Math.random() * types.length)];
            
            powerUps.add(new PowerUp(x, y, type));
        }
    }

    public boolean isFull() {
        return currentPlayers >= maxPlayers;
    }

    public boolean isEmpty() {
        return currentPlayers == 0;
    }

    public boolean canJoin() {
        return "waiting".equals(status) && !isFull();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getCurrentPlayers() { return currentPlayers; }
    public void setCurrentPlayers(int currentPlayers) { this.currentPlayers = currentPlayers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public ConcurrentHashMap<String, Player> getPlayers() { return players; }
    public void setPlayers(ConcurrentHashMap<String, Player> players) { this.players = players; }

    public List<Obstacle> getObstacles() { return obstacles; }
    public void setObstacles(List<Obstacle> obstacles) { this.obstacles = obstacles; }

    public List<PowerUp> getPowerUps() { return powerUps; }
    public void setPowerUps(List<PowerUp> powerUps) { this.powerUps = powerUps; }

    public List<Bullet> getBullets() { return bullets; }
    public void setBullets(List<Bullet> bullets) { this.bullets = bullets; }

    public String getMapType() { return mapType; }
    public void setMapType(String mapType) { this.mapType = mapType; }

    public int getMapWidth() { return mapWidth; }
    public void setMapWidth(int mapWidth) { this.mapWidth = mapWidth; }

    public int getMapHeight() { return mapHeight; }
    public void setMapHeight(int mapHeight) { this.mapHeight = mapHeight; }
}
