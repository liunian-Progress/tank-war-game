package com.tankwar.server.service;

import com.tankwar.server.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 地图和道具管理服务
 */
@Service
public class MapAndPowerUpService {

    private final Map<String, List<Obstacle>> mapObstacles = new ConcurrentHashMap<>();
    private final Map<String, List<PowerUp>> mapPowerUps = new ConcurrentHashMap<>();
    private final Map<String, List<Base>> mapBases = new ConcurrentHashMap<>();
    private final Map<String, PowerUp> activePowerUps = new ConcurrentHashMap<>();

    /**
     * 生成经典地图
     */
    public MapData generateClassicMap(String roomId) {
        List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
        List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
        List<Base> bases = new CopyOnWriteArrayList<>();

        // 生成边界墙
        generateBoundaryWalls(obstacles);
        
        // 生成中央障碍物
        generateCentralObstacles(obstacles);
        
        // 生成随机障碍物
        generateRandomObstacles(obstacles, 15);
        
        // 生成基地
        generateBases(bases);
        
        // 生成道具
        generatePowerUps(powerUps, 8);

        mapObstacles.put(roomId, obstacles);
        mapPowerUps.put(roomId, powerUps);
        mapBases.put(roomId, bases);

        return new MapData(obstacles, powerUps, bases);
    }

    /**
     * 生成竞技场地图
     */
    public MapData generateArenaMap(String roomId) {
        List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
        List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
        List<Base> bases = new CopyOnWriteArrayList<>();

        // 生成边界墙
        generateBoundaryWalls(obstacles);
        
        // 生成竞技场布局
        generateArenaLayout(obstacles);
        
        // 生成基地
        generateBases(bases);
        
        // 生成道具
        generatePowerUps(powerUps, 12);

        mapObstacles.put(roomId, obstacles);
        mapPowerUps.put(roomId, powerUps);
        mapBases.put(roomId, bases);

        return new MapData(obstacles, powerUps, bases);
    }

    /**
     * 生成迷宫地图
     */
    public MapData generateMazeMap(String roomId) {
        List<Obstacle> obstacles = new CopyOnWriteArrayList<>();
        List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
        List<Base> bases = new CopyOnWriteArrayList<>();

        // 生成边界墙
        generateBoundaryWalls(obstacles);
        
        // 生成迷宫布局
        generateMazeLayout(obstacles);
        
        // 生成基地
        generateBases(bases);
        
        // 生成道具
        generatePowerUps(powerUps, 6);

        mapObstacles.put(roomId, obstacles);
        mapPowerUps.put(roomId, powerUps);
        mapBases.put(roomId, bases);

        return new MapData(obstacles, powerUps, bases);
    }

    private void generateBoundaryWalls(List<Obstacle> obstacles) {
        // 上下边界
        obstacles.add(new Obstacle(0, 0, 800, 20, "wall"));
        obstacles.add(new Obstacle(0, 580, 800, 20, "wall"));
        
        // 左右边界
        obstacles.add(new Obstacle(0, 0, 20, 600, "wall"));
        obstacles.add(new Obstacle(780, 0, 20, 600, "wall"));
    }

    private void generateCentralObstacles(List<Obstacle> obstacles) {
        // 中央十字形障碍
        obstacles.add(new Obstacle(350, 200, 100, 20, "brick"));
        obstacles.add(new Obstacle(350, 380, 100, 20, "brick"));
        obstacles.add(new Obstacle(350, 200, 20, 200, "brick"));
        obstacles.add(new Obstacle(430, 200, 20, 200, "brick"));
        
        // 四个角落的钢铁障碍
        obstacles.add(new Obstacle(100, 100, 40, 40, "steel"));
        obstacles.add(new Obstacle(660, 100, 40, 40, "steel"));
        obstacles.add(new Obstacle(100, 460, 40, 40, "steel"));
        obstacles.add(new Obstacle(660, 460, 40, 40, "steel"));
    }

    private void generateArenaLayout(List<Obstacle> obstacles) {
        // 竞技场中央圆形障碍
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            double x = 400 + Math.cos(angle) * 150;
            double y = 300 + Math.sin(angle) * 150;
            obstacles.add(new Obstacle(x, y, 30, 30, "steel"));
        }
        
        // 外围障碍
        obstacles.add(new Obstacle(200, 150, 60, 20, "brick"));
        obstacles.add(new Obstacle(540, 150, 60, 20, "brick"));
        obstacles.add(new Obstacle(200, 430, 60, 20, "brick"));
        obstacles.add(new Obstacle(540, 430, 60, 20, "brick"));
    }

    private void generateMazeLayout(List<Obstacle> obstacles) {
        // 生成迷宫式布局
        int[][] maze = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 1) {
                    double x = j * 40;
                    double y = i * 40;
                    obstacles.add(new Obstacle(x, y, 40, 40, "brick"));
                }
            }
        }
    }

    private void generateRandomObstacles(List<Obstacle> obstacles, int count) {
        Random random = new Random();
        String[] types = {"wall", "brick", "steel"};
        
        for (int i = 0; i < count; i++) {
            double x = 50 + random.nextDouble() * 700;
            double y = 50 + random.nextDouble() * 500;
            double width = 20 + random.nextDouble() * 40;
            double height = 20 + random.nextDouble() * 40;
            String type = types[random.nextInt(types.length)];
            
            obstacles.add(new Obstacle(x, y, width, height, type));
        }
    }

    private void generateBases(List<Base> bases) {
        // 生成两个基地
        bases.add(new Base("team1", "红队", 100, 100));
        bases.add(new Base("team2", "蓝队", 700, 500));
    }

    private void generatePowerUps(List<PowerUp> powerUps, int count) {
        Random random = new Random();
        String[] types = {"speed", "damage", "health", "shield", "rapidfire", "multishot"};
        
        for (int i = 0; i < count; i++) {
            double x = 50 + random.nextDouble() * 700;
            double y = 50 + random.nextDouble() * 500;
            String type = types[random.nextInt(types.length)];
            
            powerUps.add(new PowerUp(x, y, type));
        }
    }

    /**
     * 获取房间的地图数据
     */
    public MapData getMapData(String roomId) {
        List<Obstacle> obstacles = mapObstacles.getOrDefault(roomId, new ArrayList<>());
        List<PowerUp> powerUps = mapPowerUps.getOrDefault(roomId, new ArrayList<>());
        List<Base> bases = mapBases.getOrDefault(roomId, new ArrayList<>());
        
        return new MapData(obstacles, powerUps, bases);
    }

    /**
     * 更新道具状态
     */
    public void updatePowerUps(String roomId) {
        List<PowerUp> powerUps = mapPowerUps.get(roomId);
        if (powerUps != null) {
            powerUps.forEach(powerUp -> {
                if (powerUp.isExpired()) {
                    powerUp.setActive(false);
                }
            });
        }
    }

    /**
     * 生成新道具
     */
    public void generateNewPowerUp(String roomId) {
        List<PowerUp> powerUps = mapPowerUps.get(roomId);
        if (powerUps != null && powerUps.size() < 10) {
            Random random = new Random();
            String[] types = {"speed", "damage", "health", "shield", "rapidfire", "multishot"};
            
            double x = 50 + random.nextDouble() * 700;
            double y = 50 + random.nextDouble() * 500;
            String type = types[random.nextInt(types.length)];
            
            powerUps.add(new PowerUp(x, y, type));
        }
    }

    /**
     * 清理房间数据
     */
    public void cleanupRoom(String roomId) {
        mapObstacles.remove(roomId);
        mapPowerUps.remove(roomId);
        mapBases.remove(roomId);
    }

    /**
     * 地图数据类
     */
    public static class MapData {
        private final List<Obstacle> obstacles;
        private final List<PowerUp> powerUps;
        private final List<Base> bases;

        public MapData(List<Obstacle> obstacles, List<PowerUp> powerUps, List<Base> bases) {
            this.obstacles = obstacles;
            this.powerUps = powerUps;
            this.bases = bases;
        }

        public List<Obstacle> getObstacles() { return obstacles; }
        public List<PowerUp> getPowerUps() { return powerUps; }
        public List<Base> getBases() { return bases; }
    }
}
