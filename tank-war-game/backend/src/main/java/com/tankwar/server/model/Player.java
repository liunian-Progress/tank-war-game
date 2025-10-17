package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 玩家实体类
 */
public class Player {
    private String id;
    private String name;
    private double x;
    private double y;
    private int direction; // 0:上, 1:右, 2:下, 3:左
    private int health;
    private int maxHealth;
    private int score;
    private int kills;
    private int deaths;
    private String color;
    private double speed;
    private boolean isAlive;
    private LocalDateTime lastActiveTime;
    private int powerUpLevel;
    private String powerUpType;

    public Player() {
        this.id = UUID.randomUUID().toString();
        this.health = 100;
        this.maxHealth = 100;
        this.score = 0;
        this.kills = 0;
        this.deaths = 0;
        this.speed = 3.0;
        this.isAlive = true;
        this.lastActiveTime = LocalDateTime.now();
        this.powerUpLevel = 0;
        this.powerUpType = "none";
        this.direction = 0;
    }

    public Player(String name, double x, double y) {
        this();
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = generateRandomColor();
    }

    private String generateRandomColor() {
        String[] colors = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", 
            "#FECA57", "#FF9FF3", "#54A0FF", "#5F27CD"
        };
        return colors[(int) (Math.random() * colors.length)];
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }

    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public int getPowerUpLevel() { return powerUpLevel; }
    public void setPowerUpLevel(int powerUpLevel) { this.powerUpLevel = powerUpLevel; }

    public String getPowerUpType() { return powerUpType; }
    public void setPowerUpType(String powerUpType) { this.powerUpType = powerUpType; }

    public void updatePosition(double x, double y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.lastActiveTime = LocalDateTime.now();
    }

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
        if (this.health <= 0) {
            this.isAlive = false;
            this.deaths++;
        }
    }

    public void heal(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
    }

    public void addKill() {
        this.kills++;
        this.score += 100;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void respawn(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = this.maxHealth;
        this.isAlive = true;
        this.direction = 0;
    }
}
