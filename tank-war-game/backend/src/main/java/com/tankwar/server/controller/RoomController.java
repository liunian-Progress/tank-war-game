package com.tankwar.server.controller;

import com.tankwar.server.model.GameRoom;
import com.tankwar.server.model.Player;
import com.tankwar.server.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 房间管理API控制器
 */
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * 获取房间列表
     */
    @GetMapping
    public List<GameRoom> getRoomList() {
        return roomService.getRoomList();
    }

    /**
     * 创建房间
     */
    @PostMapping("/create")
    public GameRoom createRoom(@RequestBody Map<String, Object> request) {
        String roomName = (String) request.get("name");
        Integer maxPlayers = (Integer) request.get("maxPlayers");
        
        if (roomName == null || roomName.trim().isEmpty()) {
            roomName = "房间" + System.currentTimeMillis();
        }
        
        if (maxPlayers == null || maxPlayers < 2 || maxPlayers > 8) {
            maxPlayers = 8;
        }
        
        return roomService.createRoom(roomName, maxPlayers);
    }

    /**
     * 获取房间信息
     */
    @GetMapping("/{roomId}")
    public GameRoom getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }

    /**
     * 加入房间
     */
    @PostMapping("/{roomId}/join")
    public Map<String, Object> joinRoom(@PathVariable String roomId, @RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        
        if (playerName == null || playerName.trim().isEmpty()) {
            return Map.of("success", false, "message", "玩家名称不能为空");
        }
        
        Player player = new Player(playerName, 100, 100);
        boolean success = roomService.joinRoom(roomId, player);
        
        if (success) {
            return Map.of(
                "success", true, 
                "message", "加入房间成功",
                "playerId", player.getId(),
                "room", roomService.getRoom(roomId)
            );
        } else {
            return Map.of("success", false, "message", "房间已满或不存在");
        }
    }

    /**
     * 快速匹配
     */
    @PostMapping("/quick-match")
    public Map<String, Object> quickMatch(@RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        
        if (playerName == null || playerName.trim().isEmpty()) {
            return Map.of("success", false, "message", "玩家名称不能为空");
        }
        
        Player player = new Player(playerName, 100, 100);
        GameRoom room = roomService.quickMatch(player);
        
        return Map.of(
            "success", true,
            "message", "匹配成功",
            "playerId", player.getId(),
            "room", room
        );
    }

    /**
     * 获取房间内玩家列表
     */
    @GetMapping("/{roomId}/players")
    public List<Player> getRoomPlayers(@PathVariable String roomId) {
        return roomService.getRoomPlayers(roomId);
    }

    /**
     * 获取等待中的房间
     */
    @GetMapping("/waiting")
    public List<GameRoom> getWaitingRooms() {
        return roomService.getWaitingRooms();
    }

    /**
     * 获取进行中的房间
     */
    @GetMapping("/playing")
    public List<GameRoom> getPlayingRooms() {
        return roomService.getPlayingRooms();
    }

    /**
     * 获取房间统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getRoomStats() {
        return Map.of(
            "totalRooms", roomService.getRoomCount(),
            "totalPlayers", roomService.getTotalPlayers(),
            "waitingRooms", roomService.getWaitingRooms().size(),
            "playingRooms", roomService.getPlayingRooms().size()
        );
    }
}
