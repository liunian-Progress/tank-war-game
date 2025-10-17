package com.tankwar.server.controller;

import com.tankwar.server.service.MapAndPowerUpService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 地图和道具API控制器
 */
@RestController
@RequestMapping("/api/map")
@CrossOrigin(origins = "*")
public class MapController {

    private final MapAndPowerUpService mapAndPowerUpService;

    public MapController(MapAndPowerUpService mapAndPowerUpService) {
        this.mapAndPowerUpService = mapAndPowerUpService;
    }

    /**
     * 生成经典地图
     */
    @PostMapping("/{roomId}/classic")
    public MapAndPowerUpService.MapData generateClassicMap(@PathVariable String roomId) {
        return mapAndPowerUpService.generateClassicMap(roomId);
    }

    /**
     * 生成竞技场地图
     */
    @PostMapping("/{roomId}/arena")
    public MapAndPowerUpService.MapData generateArenaMap(@PathVariable String roomId) {
        return mapAndPowerUpService.generateArenaMap(roomId);
    }

    /**
     * 生成迷宫地图
     */
    @PostMapping("/{roomId}/maze")
    public MapAndPowerUpService.MapData generateMazeMap(@PathVariable String roomId) {
        return mapAndPowerUpService.generateMazeMap(roomId);
    }

    /**
     * 获取地图数据
     */
    @GetMapping("/{roomId}")
    public MapAndPowerUpService.MapData getMapData(@PathVariable String roomId) {
        return mapAndPowerUpService.getMapData(roomId);
    }

    /**
     * 生成新道具
     */
    @PostMapping("/{roomId}/powerup")
    public Map<String, Object> generateNewPowerUp(@PathVariable String roomId) {
        mapAndPowerUpService.generateNewPowerUp(roomId);
        return Map.of("success", true, "message", "新道具已生成");
    }

    /**
     * 清理房间地图数据
     */
    @DeleteMapping("/{roomId}")
    public Map<String, Object> cleanupRoom(@PathVariable String roomId) {
        mapAndPowerUpService.cleanupRoom(roomId);
        return Map.of("success", true, "message", "房间地图数据已清理");
    }
}
