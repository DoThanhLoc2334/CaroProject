/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class RoomManager {
    private final List<Room> rooms = new ArrayList<>();

    public synchronized Room createRoom(String creator) {
        String roomId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Room room = new Room(roomId, creator);
        rooms.add(room);
        System.out.println("[RoomManager] Room created: " + room);
        return room;
    }

    public synchronized Room joinRoom(String roomId, String username) {
        Room room = getRoomById(roomId);
        if (room == null) {
            System.out.println("[RoomManager] Room not found: " + roomId);
            return null;
        }
        if (room.isFull()) {
            System.out.println("[RoomManager] Room is full: " + roomId);
            return null;
        }
        if (room.getPlayer1().equals(username)) {
            System.out.println("[RoomManager] Player is already in the room: " + username);
            return null;
        }

        room.setPlayer2(username);
        room.setStatus("PLAYING"); // giữ nguyên logic của bạn
        System.out.println("[RoomManager] " + username + " joined room " + roomId);
        return room;
    }

    public synchronized List<Room> getWaitingRooms() {
        List<Room> waiting = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isWaiting()) {
                waiting.add(room);
            }
        }
        return waiting;
    }

    public synchronized Room getRoomById(String roomId) {
        for (Room room : rooms) {
            if (room.getId().equalsIgnoreCase(roomId)) {
                return room;
            }
        }
        return null;
    }

    public synchronized boolean removeRoom(String roomId) {
        Room room = getRoomById(roomId);
        if (room != null) {
            rooms.remove(room);
            System.out.println("[RoomManager] Room removed: " + roomId);
            return true;
        }
        return false;
    }

    public synchronized void printAllRooms() {
        System.out.println("=== Current Rooms (" + rooms.size() + ") ===");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    // Thêm hàm phụ để tương thích với ClientHandler
    public synchronized ArrayList<String> getRoomNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Room room : rooms) {
            names.add(room.getId() + " - " + room.getPlayer1() + " (" + room.getStatus() + ")");
        }
        return names;
    }

    // ===================== A2: Auto-start & broadcast =====================

    /** Gửi danh sách người trong phòng cho tất cả người chơi.
     *  Format: JOINED_ROOM|<roomId>|name1,name2
     */
    public synchronized void broadcastPlayers(Room room, BiConsumer<String, String> sendToUser) {
        String csv = buildPlayersCsv(room);
        String msg = "JOINED_ROOM|" + room.getId() + "|" + csv;

        if (room.getPlayer1() != null) sendToUser.accept(room.getPlayer1(), msg);
        if (room.getPlayer2() != null) sendToUser.accept(room.getPlayer2(), msg);
        System.out.println("[RoomManager] Broadcast players: " + msg);
    }

    /** Khi đủ 2 user trong cùng phòng: chuyển WAITING -> PLAYING và báo GAME_STARTED cho 2 phía.
     *  Format: GAME_STARTED|<roomId>|<youAre>|<turn>|<boardSize>
     */
    public synchronized void onPlayerJoined(Room room, BiConsumer<String, String> sendToUser) {
        // điều kiện an toàn: cần đủ 2 người và trạng thái không phải FINISHED
        if (room == null) return;

        boolean hasP1 = room.getPlayer1() != null && !room.getPlayer1().isEmpty();
        boolean hasP2 = room.getPlayer2() != null && !room.getPlayer2().isEmpty();
        if (!hasP1 || !hasP2) return;

        // Nếu phòng đang WAITING thì chuyển sang PLAYING
        if (room.isWaiting() || "WAITING".equalsIgnoreCase(room.getStatus())) {
            room.setStatus("PLAYING");
        }

        // Thông số ván (giữ cố định theo yêu cầu)
        String roomId = room.getId();
        int boardSize = 20;
        String turn = "X"; // X đi trước

        String p1 = room.getPlayer1(); // X
        String p2 = room.getPlayer2(); // O

        String msgToP1 = "GAME_STARTED|" + roomId + "|X|" + turn + "|" + boardSize;
        String msgToP2 = "GAME_STARTED|" + roomId + "|O|" + turn + "|" + boardSize;

        sendToUser.accept(p1, msgToP1);
        sendToUser.accept(p2, msgToP2);

        System.out.println("[RoomManager] Auto-start room " + roomId +
                " -> send GAME_STARTED to [" + p1 + "=X, " + p2 + "=O]");
    }

    // Helper: build "name1,name2"
    private String buildPlayersCsv(Room room) {
        StringBuilder sb = new StringBuilder();
        if (room.getPlayer1() != null) sb.append(room.getPlayer1());
        if (room.getPlayer2() != null) {
            if (sb.length() > 0) sb.append(",");
            sb.append(room.getPlayer2());
        }
        return sb.toString();
    }
}
