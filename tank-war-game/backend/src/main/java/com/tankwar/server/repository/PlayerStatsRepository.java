package com.tankwar.server.repository;

import com.tankwar.server.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 玩家统计数据访问接口
 */
@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

    /**
     * 根据玩家名称查找统计信息
     */
    PlayerStats findByPlayerName(String playerName);

    /**
     * 获取总积分排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p ORDER BY p.totalScore DESC")
    List<PlayerStats> findTop10ByTotalScore();

    /**
     * 获取击杀排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p ORDER BY p.totalKills DESC")
    List<PlayerStats> findTop10ByTotalKills();

    /**
     * 获取胜率排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p WHERE p.gamesPlayed >= 5 ORDER BY (p.gamesWon * 1.0 / p.gamesPlayed) DESC")
    List<PlayerStats> findTop10ByWinRate();

    /**
     * 获取K/D比排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p WHERE p.totalDeaths > 0 ORDER BY (p.totalKills * 1.0 / p.totalDeaths) DESC")
    List<PlayerStats> findTop10ByKillDeathRatio();

    /**
     * 获取游戏场次排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p ORDER BY p.gamesPlayed DESC")
    List<PlayerStats> findTop10ByGamesPlayed();

    /**
     * 获取总游戏时间排行榜（前10名）
     */
    @Query("SELECT p FROM PlayerStats p ORDER BY p.totalPlayTime DESC")
    List<PlayerStats> findTop10ByTotalPlayTime();

    /**
     * 检查玩家是否存在
     */
    boolean existsByPlayerName(String playerName);
}
