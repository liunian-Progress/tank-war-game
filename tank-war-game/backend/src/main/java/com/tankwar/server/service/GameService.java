package com.tankwar.server.service;

import com.tankwar.server.model.*;
import com.tankwar.server.repository.PlayerStatsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 游戏服务类
 */
@Service
public class GameService {

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToPlayerId = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private final List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private final GameState gameState = new GameState();
    private final PlayerStatsRepository playerStatsRepository;
    
    private static final int MAX_PLAYERS = 8;
    private static final int MAP_WIDTH = 800;
    private static final int MAP_HEIGHT = 600;

    public GameService(PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
        initializeGame();
    }

    private void initializeGame() {
        // 初始化障碍物
        generateObstacles();
        
        // 初始化道具
        generatePowerUps();
        
        // 设置游戏状态
        gameState.setPlayers(players);
        gameState.setBullets(bullets);
        gameState.setObstacles(obstacles);
        gameState.setPowerUps(powerUps);
        gameState.setMapWidth(MAP_WIDTH);
        gameState.setMapHeight(MAP_HEIGHT);
    }

    public Player addPlayer(String playerName, String sessionId) {
        if (players.size() >= MAX_PLAYERS) {
            throw new RuntimeException("游戏房间已满");
        }

        // 生成随机出生点
        double x = 50 + Math.random() * (MAP_WIDTH - 100);
        double y = 50 + Math.random() * (MAP_HEIGHT - 100);
        
        // 确保不与其他玩家和障碍物重叠
        // 创建一个临时玩家ID用于碰撞检查
        String tempId = "temp_" + System.currentTimeMillis();
        int attempts = 0;
        int maxAttempts = 100;
        
        while (isPositionOccupied(x, y) || isPositionCollidingWithObstacles(x, y)) {
            attempts++;
            if (attempts > maxAttempts) {
                // 如果尝试次数过多，重置到默认位置
                x = 50;
                y = 50;
                break;
            }
            x = 50 + Math.random() * (MAP_WIDTH - 100);
            y = 50 + Math.random() * (MAP_HEIGHT - 100);
        }

        Player player = new Player(playerName, x, y);
        
        // 从数据库中获取玩家的历史分数
        PlayerStats stats = playerStatsRepository.findByPlayerName(playerName);
        if (stats != null) {
            // 设置玩家初始分数为数据库中的总分数
            player.setScore(stats.getTotalScore());
            player.setKills(stats.getTotalKills());
            player.setDeaths(stats.getTotalDeaths());
        }
        
        players.put(player.getId(), player);
        sessionToPlayerId.put(sessionId, player.getId());
        
        return player;
    }

    public Player removePlayer(String sessionId) {
        String playerId = sessionToPlayerId.remove(sessionId);
        if (playerId != null) {
            return players.remove(playerId);
        }
        return null;
    }

    public void updatePlayerPosition(String playerId, double x, double y, int direction) {
        Player player = players.get(playerId);
        if (player != null && player.isAlive()) {
            // 检查边界
            x = Math.max(20, Math.min(MAP_WIDTH - 20, x));
            y = Math.max(20, Math.min(MAP_HEIGHT - 20, y));
            
            // 检查碰撞
            if (!isCollision(x, y, playerId)) {
                player.updatePosition(x, y, direction);
            }
        }
    }

    public Bullet createBullet(String playerId, double x, double y, int direction) {
        Player player = players.get(playerId);
        if (player == null || !player.isAlive()) {
            return null;
        }

        Bullet bullet = new Bullet(playerId, x, y, direction);
        bullets.add(bullet);
        
        return bullet;
    }

    public void updateGame() {
        updateBullets();
        updatePowerUps();
        checkCollisions();
        removeInactiveObjects();
    }

    private void updateBullets() {
        bullets.forEach(bullet -> {
            if (bullet.isActive()) {
                bullet.update();
                
                // 检查边界
                if (bullet.getX() < 0 || bullet.getX() > MAP_WIDTH || 
                    bullet.getY() < 0 || bullet.getY() > MAP_HEIGHT) {
                    bullet.setActive(false);
                }
            }
        });
    }

    private void updatePowerUps() {
        powerUps.forEach(powerUp -> {
            if (powerUp.isExpired()) {
                powerUp.setActive(false);
            }
        });
    }

    private void checkCollisions() {
        // 子弹与玩家碰撞
        bullets.stream()
                .filter(Bullet::isActive)
                .forEach(bullet -> {
                    players.values().stream()
                            .filter(player -> !player.getId().equals(bullet.getPlayerId()) && player.isAlive())
                            .forEach(player -> {
                                if (isColliding(bullet.getX(), bullet.getY(), 3, 
                                              player.getX(), player.getY(), 20, 20)) {
                                    player.takeDamage(bullet.getDamage());
                                    bullet.setActive(false);
                                    
                                    // 击杀统计
                                    Player shooter = players.get(bullet.getPlayerId());
                                    if (shooter != null && !player.isAlive()) {
                                        shooter.addKill();
                                    }
                                }
                            });
                });

        // 子弹与障碍物碰撞
        bullets.stream()
                .filter(Bullet::isActive)
                .forEach(bullet -> {
                    obstacles.forEach(obstacle -> {
                        if (isColliding(bullet.getX(), bullet.getY(), 3,
                                      obstacle.getX(), obstacle.getY(), 
                                      obstacle.getWidth(), obstacle.getHeight())) {
                            obstacle.takeDamage(bullet.getDamage());
                            bullet.setActive(false);
                        }
                    });
                });

        // 玩家与道具碰撞
        players.values().stream()
                .filter(Player::isAlive)
                .forEach(player -> {
                    powerUps.stream()
                            .filter(PowerUp::isActive)
                            .forEach(powerUp -> {
                                if (isColliding(player.getX(), player.getY(), 20,
                                              powerUp.getX(), powerUp.getY(), 
                                              powerUp.getRadius() * 2, powerUp.getRadius() * 2)) {
                                    applyPowerUp(player, powerUp);
                                    powerUp.setActive(false);
                                }
                            });
                });
    }

    private void applyPowerUp(Player player, PowerUp powerUp) {
        switch (powerUp.getType()) {
            case "speed":
                player.setSpeed(Math.min(player.getSpeed() + 1, 6));
                break;
            case "damage":
                player.setPowerUpLevel(player.getPowerUpLevel() + 1);
                player.setPowerUpType("damage");
                break;
            case "health":
                player.heal(50);
                break;
            case "shield":
                player.setPowerUpLevel(player.getPowerUpLevel() + 1);
                player.setPowerUpType("shield");
                break;
        }
        player.addScore(10);
    }

    private void removeInactiveObjects() {
        bullets.removeIf(bullet -> !bullet.isActive());
        powerUps.removeIf(powerUp -> !powerUp.isActive());
        obstacles.removeIf(Obstacle::isDestroyed);
    }

    private boolean isPositionOccupied(double x, double y) {
        return players.values().stream()
                .anyMatch(player -> isColliding(x, y, 20, player.getX(), player.getY(), 20, 20));
    }
    
    /**
     * 检查给定位置是否与障碍物碰撞
     */
    private boolean isPositionCollidingWithObstacles(double x, double y) {
        return obstacles.stream()
                .anyMatch(obstacle -> isColliding(x, y, 20, obstacle.getX(), obstacle.getY(), 
                                                obstacle.getWidth(), obstacle.getHeight()));
    }

    private boolean isCollision(double x, double y, String playerId) {
        // 检查与障碍物碰撞
        boolean obstacleCollision = obstacles.stream()
                .anyMatch(obstacle -> isColliding(x, y, 20, obstacle.getX(), obstacle.getY(), 
                                                obstacle.getWidth(), obstacle.getHeight()));
        
        if (obstacleCollision) return true;

        // 检查与其他玩家碰撞
        return players.values().stream()
                .filter(player -> !player.getId().equals(playerId))
                .anyMatch(player -> isColliding(x, y, 20, player.getX(), player.getY(), 20, 20));
    }

    private boolean isColliding(double x1, double y1, double r1, double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + r1 > x2 && y1 < y2 + h2 && y1 + r1 > y2;
    }

    private void generateObstacles() {
        Random random = new Random();
        
        // 生成随机障碍物
        for (int i = 0; i < 15; i++) {
            double x = random.nextDouble() * (MAP_WIDTH - 50);
            double y = random.nextDouble() * (MAP_HEIGHT - 50);
            double width = 30 + random.nextDouble() * 20;
            double height = 30 + random.nextDouble() * 20;
            
            String[] types = {"wall", "brick", "steel"};
            String type = types[random.nextInt(types.length)];
            
            obstacles.add(new Obstacle(x, y, width, height, type));
        }
    }

    private void generatePowerUps() {
        Random random = new Random();
        String[] types = {"speed", "damage", "health", "shield"};
        
        for (int i = 0; i < 5; i++) {
            double x = random.nextDouble() * (MAP_WIDTH - 20);
            double y = random.nextDouble() * (MAP_HEIGHT - 20);
            String type = types[random.nextInt(types.length)];
            
            powerUps.add(new PowerUp(x, y, type));
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Player> getLeaderboard() {
        return players.values().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                .collect(Collectors.toList());
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isGameFull() {
        return players.size() >= MAX_PLAYERS;
    }
    
    /**
     * 复活玩家
     */
    public Player respawnPlayer(String playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            return null;
        }
        
        // 保存当前分数到数据库
        savePlayerScoreToDatabase(player);
        
        // 生成随机重生点
        double x = 50 + Math.random() * (MAP_WIDTH - 100);
        double y = 50 + Math.random() * (MAP_HEIGHT - 100);
        
        // 确保不与其他玩家和障碍物重叠
        int attempts = 0;
        int maxAttempts = 100;
        
        while (isPositionOccupied(x, y) || isPositionCollidingWithObstacles(x, y)) {
            attempts++;
            if (attempts > maxAttempts) {
                // 如果尝试次数过多，重置到默认位置
                x = 50;
                y = 50;
                break;
            }
            x = 50 + Math.random() * (MAP_WIDTH - 100);
            y = 50 + Math.random() * (MAP_HEIGHT - 100);
        }
        
        // 保存当前分数
        int currentScore = player.getScore();
        int currentKills = player.getKills();
        int currentDeaths = player.getDeaths();
        
        // 调用玩家的respawn方法
        player.respawn(x, y);
        
        // 恢复分数和统计数据
        player.setScore(currentScore);
        player.setKills(currentKills);
        player.setDeaths(currentDeaths);
        
        return player;
    }
    
    /**
     * 保存玩家分数到数据库
     */
    private void savePlayerScoreToDatabase(Player player) {
        PlayerStats stats = playerStatsRepository.findByPlayerName(player.getName());
        
        if (stats == null) {
            stats = new PlayerStats(player.getName());
        }
        
        // 更新数据库中的总分数为当前玩家的分数
        stats.setTotalScore(player.getScore());
        stats.setTotalKills(player.getKills());
        stats.setTotalDeaths(player.getDeaths());
        stats.setLastPlayTime(java.time.LocalDateTime.now());
        
        playerStatsRepository.save(stats);
    }
}
