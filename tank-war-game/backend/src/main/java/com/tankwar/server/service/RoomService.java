package com.tankwar.server.service;

import com.tankwar.server.model.GameRoom;
import com.tankwar.server.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 房间管理服务
 */
@Service
public class RoomService {

    private final ConcurrentHashMap<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> playerToRoom = new ConcurrentHashMap<>();

    /**
     * 创建新房间
     */
    public GameRoom createRoom(String roomName, int maxPlayers) {
        GameRoom room = new GameRoom(roomName, maxPlayers);
        rooms.put(room.getId(), room);
        System.out.println("创建新房间: " + roomName + " (ID: " + room.getId() + ")");
        return room;
    }

    /**
     * 加入房间
     */
    public boolean joinRoom(String roomId, Player player) {
        GameRoom room = rooms.get(roomId);
        if (room != null && room.canJoin()) {
            if (room.addPlayer(player)) {
                playerToRoom.put(player.getId(), roomId);
                System.out.println("玩家 " + player.getName() + " 加入房间 " + room.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * 离开房间
     */
    public void leaveRoom(String playerId) {
        String roomId = playerToRoom.remove(playerId);
        if (roomId != null) {
            GameRoom room = rooms.get(roomId);
            if (room != null) {
                room.removePlayer(playerId);
                
                // 如果房间空了，删除房间
                if (room.isEmpty()) {
                    rooms.remove(roomId);
                    System.out.println("房间 " + room.getName() + " 已删除（无玩家）");
                }
            }
        }
    }

    /**
     * 快速匹配
     */
    public GameRoom quickMatch(Player player) {
        // 查找有空位的房间
        Optional<GameRoom> availableRoom = rooms.values().stream()
                .filter(GameRoom::canJoin)
                .min(Comparator.comparing(GameRoom::getCurrentPlayers));
        
        if (availableRoom.isPresent()) {
            GameRoom room = availableRoom.get();
            if (joinRoom(room.getId(), player)) {
                return room;
            }
        }
        
        // 如果没有可用房间，创建新房间
        GameRoom newRoom = createRoom("快速匹配房间", 8);
        joinRoom(newRoom.getId(), player);
        return newRoom;
    }

    /**
     * 获取房间列表
     */
    public List<GameRoom> getRoomList() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(GameRoom::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取房间信息
     */
    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * 获取玩家所在房间
     */
    public GameRoom getPlayerRoom(String playerId) {
        String roomId = playerToRoom.get(playerId);
        return roomId != null ? rooms.get(roomId) : null;
    }

    /**
     * 获取房间内所有玩家
     */
    public List<Player> getRoomPlayers(String roomId) {
        GameRoom room = rooms.get(roomId);
        return room != null ? new ArrayList<>(room.getPlayers().values()) : new ArrayList<>();
    }

    /**
     * 检查房间是否存在
     */
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }

    /**
     * 获取房间数量
     */
    public int getRoomCount() {
        return rooms.size();
    }

    /**
     * 获取在线玩家总数
     */
    public int getTotalPlayers() {
        return playerToRoom.size();
    }

    /**
     * 清理空房间
     */
    public void cleanupEmptyRooms() {
        rooms.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * 获取等待中的房间
     */
    public List<GameRoom> getWaitingRooms() {
        return rooms.values().stream()
                .filter(room -> "waiting".equals(room.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 获取进行中的房间
     */
    public List<GameRoom> getPlayingRooms() {
        return rooms.values().stream()
                .filter(room -> "playing".equals(room.getStatus()))
                .collect(Collectors.toList());
    }
}
