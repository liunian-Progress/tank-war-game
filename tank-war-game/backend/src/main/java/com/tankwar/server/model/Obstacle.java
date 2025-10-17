package com.tankwar.server.model;

/**
 * 障碍物实体类
 */
public class Obstacle {
    private double x;
    private double y;
    private double width;
    private double height;
    private String type; // wall, brick, steel
    private int health;
    private boolean destructible;

    public Obstacle() {
        this.type = "wall";
        this.health = 100;
        this.destructible = false;
    }

    public Obstacle(double x, double y, double width, double height, String type) {
        this();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        
        // 根据类型设置属性
        switch (type) {
            case "brick":
                this.destructible = true;
                this.health = 50;
                break;
            case "steel":
                this.destructible = false;
                this.health = 200;
                break;
            case "wall":
            default:
                this.destructible = false;
                this.health = 100;
                break;
        }
    }

    public void takeDamage(int damage) {
        if (this.destructible) {
            this.health = Math.max(0, this.health - damage);
        }
    }

    public boolean isDestroyed() {
        return this.destructible && this.health <= 0;
    }

    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isDestructible() { return destructible; }
    public void setDestructible(boolean destructible) { this.destructible = destructible; }
}
