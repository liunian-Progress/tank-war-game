package com.tankwar.server.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 玩家统计实体类（用于排行榜）
 */
@Entity
@Table(name = "player_stats")
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String playerName;
    
    @Column(nullable = false)
    private int totalScore = 0;
    
    @Column(nullable = false)
    private int totalKills = 0;
    
    @Column(nullable = false)
    private int totalDeaths = 0;
    
    @Column(nullable = false)
    private int gamesPlayed = 0;
    
    @Column(nullable = false)
    private int gamesWon = 0;
    
    @Column(nullable = false)
    private int totalPlayTime = 0; // 总游戏时间（秒）
    
    @Column(nullable = false)
    private LocalDateTime lastPlayTime = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime firstPlayTime = LocalDateTime.now();

    public PlayerStats() {}

    public PlayerStats(String playerName) {
        this.playerName = playerName;
    }

    public void updateStats(Player player, boolean won, int playTime) {
        this.totalScore += player.getScore();
        this.totalKills += player.getKills();
        this.totalDeaths += player.getDeaths();
        this.gamesPlayed++;
        this.totalPlayTime += playTime;
        this.lastPlayTime = LocalDateTime.now();
        
        if (won) {
            this.gamesWon++;
        }
    }

    public double getWinRate() {
        return gamesPlayed > 0 ? (double) gamesWon / gamesPlayed : 0.0;
    }

    public double getKillDeathRatio() {
        return totalDeaths > 0 ? (double) totalKills / totalDeaths : totalKills;
    }

    public double getAverageScore() {
        return gamesPlayed > 0 ? (double) totalScore / gamesPlayed : 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int totalKills) { this.totalKills = totalKills; }

    public int getTotalDeaths() { return totalDeaths; }
    public void setTotalDeaths(int totalDeaths) { this.totalDeaths = totalDeaths; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getGamesWon() { return gamesWon; }
    public void setGamesWon(int gamesWon) { this.gamesWon = gamesWon; }

    public int getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(int totalPlayTime) { this.totalPlayTime = totalPlayTime; }

    public LocalDateTime getLastPlayTime() { return lastPlayTime; }
    public void setLastPlayTime(LocalDateTime lastPlayTime) { this.lastPlayTime = lastPlayTime; }

    public LocalDateTime getFirstPlayTime() { return firstPlayTime; }
    public void setFirstPlayTime(LocalDateTime firstPlayTime) { this.firstPlayTime = firstPlayTime; }
}
