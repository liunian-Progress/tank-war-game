package com.tankwar.server.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 道具实体类
 */
public class PowerUp {
    private String id;
    private double x;
    private double y;
    private String type; // speed, damage, health, shield
    private String color;
    private int radius;
    private boolean active;
    private LocalDateTime createTime;
    private int duration; // 持续时间(秒)

    public PowerUp() {
        this.id = UUID.randomUUID().toString();
        this.radius = 8;
        this.active = true;
        this.createTime = LocalDateTime.now();
        this.duration = 30;
    }

    public PowerUp(double x, double y, String type) {
        this();
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = getColorByType(type);
    }

    private String getColorByType(String type) {
        switch (type) {
            case "speed": return "#FF6B6B";
            case "damage": return "#4ECDC4";
            case "health": return "#45B7D1";
            case "shield": return "#96CEB4";
            default: return "#FFFFFF";
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createTime.plusSeconds(duration));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
}
