package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 基地实体类
 */
public class Base {
    private String id;
    private String teamId;
    private String teamName;
    private double x;
    private double y;
    private double width;
    private double height;
    private int health;
    private int maxHealth;
    private boolean destroyed;
    private String color;
    private LocalDateTime createTime;

    public Base() {
        this.id = UUID.randomUUID().toString();
        this.maxHealth = 1000;
        this.health = maxHealth;
        this.destroyed = false;
        this.width = 60;
        this.height = 60;
        this.createTime = LocalDateTime.now();
    }

    public Base(String teamId, String teamName, double x, double y) {
        this();
        this.teamId = teamId;
        this.teamName = teamName;
        this.x = x;
        this.y = y;
        this.color = generateTeamColor(teamId);
    }

    private String generateTeamColor(String teamId) {
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4"};
        int hash = teamId.hashCode();
        return colors[Math.abs(hash) % colors.length];
    }

    public void takeDamage(int damage) {
        this.health = Math.max(0, this.health - damage);
        if (this.health <= 0) {
            this.destroyed = true;
        }
    }

    public void repair(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
        if (this.health > 0) {
            this.destroyed = false;
        }
    }

    public double getHealthPercentage() {
        return (double) this.health / this.maxHealth;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public boolean isDestroyed() { return destroyed; }
    public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
