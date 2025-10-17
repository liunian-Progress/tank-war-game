package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 子弹实体类
 */
public class Bullet {
    private String id;
    private String playerId;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private int damage;
    private double speed;
    private LocalDateTime createTime;
    private boolean active;

    public Bullet() {
        this.id = UUID.randomUUID().toString();
        this.damage = 25;
        this.speed = 8.0;
        this.createTime = LocalDateTime.now();
        this.active = true;
    }

    public Bullet(String playerId, double x, double y, int direction) {
        this();
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        
        // 根据方向设置速度
        switch (direction) {
            case 0: // 上
                this.vx = 0;
                this.vy = -this.speed;
                break;
            case 1: // 右
                this.vx = this.speed;
                this.vy = 0;
                break;
            case 2: // 下
                this.vx = 0;
                this.vy = this.speed;
                break;
            case 3: // 左
                this.vx = -this.speed;
                this.vy = 0;
                break;
        }
    }

    public void update() {
        this.x += this.vx;
        this.y += this.vy;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }

    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
