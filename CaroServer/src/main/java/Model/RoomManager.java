package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

// Class quản lý các phòng chơi
public class RoomManager {
    private List<Room> rooms = new ArrayList<>();
    // Tạo phòng mới
    public synchronized Room createRoom(String creator) {
        System.out.println("Creating room for: " + creator);
        // Tạo ID phòng ngẫu nhiên
        String roomId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // Tạo phòng mới
        Room room = new Room(roomId, creator);
        // Thêm vào danh sách
//        rooms.add(room);
//        System.out.println("Room created: " + room);
//        System.out.println("Total rooms: " + rooms.size());
        return room;
    }

    // Tham gia phòng chơi
    public synchronized Room joinRoom(String roomId, String username) {
        System.out.println("User " + username + " wants to join room " + roomId);
        
        // Tìm phòng theo ID
        Room room = getRoomById(roomId);
        if (room == null) {
            System.out.println("Room not found: " + roomId);
            return null;
        }
        
        // Kiểm tra phòng đã đầy chưa
        if (room.isFull()) {
            System.out.println("Room is full: " + roomId);
            return null;
        }
        
        // Kiểm tra user đã ở trong phòng chưa
        if (room.getPlayer1().equals(username)) {
            System.out.println("User already in room: " + username);
            return null;
        }

        // Thêm user vào phòng
        room.setPlayer2(username);
        room.setStatus("PLAYING");
        
        System.out.println(username + " has joined room " + roomId);
        System.out.println("Room contains: " + room.getPlayer1() + " and " + room.getPlayer2());
        
        return room;
    }

    // Lấy danh sách phòng đang chờ
    public synchronized List<Room> getWaitingRooms() {
        System.out.println("Fetching waiting rooms...");
        
        List<Room> waiting = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isWaiting()) {
                waiting.add(room);
            }
        }
        
        System.out.println("Found " + waiting.size() + " waiting rooms");
        return waiting;
    }

    // Tìm phòng theo ID
    public synchronized Room getRoomById(String roomId) {
        System.out.println("Looking for room by ID: " + roomId);
        
        for (Room room : rooms) {
            if (room.getId().equalsIgnoreCase(roomId)) {
                System.out.println("Found room: " + room);
                return room;
            }
        }
        
        System.out.println("Room not found: " + roomId);
        return null;
    }

    // Xóa phòng khỏi danh sách
    public synchronized boolean removeRoom(String roomId) {
        System.out.println("Removing room: " + roomId);
        
        Room room = getRoomById(roomId);
        if (room != null) {
            rooms.remove(room);
            System.out.println("Removed room: " + roomId);
            System.out.println("Remaining rooms: " + rooms.size());
            return true;
        }
        
        System.out.println("Room to delete not found: " + roomId);
        return false;
    }

    // In tất cả phòng hiện tại
    public synchronized void printAllRooms() {
        System.out.println("=== ALL ROOMS LIST (" + rooms.size() + ") ===");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    // Lấy danh sách tên phòng
    public synchronized ArrayList<String> getRoomNames() {
        System.out.println("Fetching room names...");
        
        ArrayList<String> names = new ArrayList<>();
        for (Room room : rooms) {
            String roomInfo = room.getId() + " - " + room.getPlayer1() + " (" + room.getStatus() + ")";
            names.add(roomInfo);
        }
        
        System.out.println("Retrieved " + names.size() + " room names");
        return names;
    }

    // Broadcast danh sách người chơi trong phòng
    public synchronized void broadcastPlayers(Room room, BiConsumer<String, String> sendToUser) {
        System.out.println("Broadcasting player list for room " + room.getId());
        
        String csv = buildPlayersCsv(room);
        String msg = "JOINED_ROOM|" + room.getId() + "|" + csv;

        // Gửi tin nhắn cho player 1
        if (room.getPlayer1() != null) {
            sendToUser.accept(room.getPlayer1(), msg);
            System.out.println("Sent to " + room.getPlayer1() + ": " + msg);
        }
        
        // Gửi tin nhắn cho player 2
        if (room.getPlayer2() != null) {
            sendToUser.accept(room.getPlayer2(), msg);
            System.out.println("Sent to " + room.getPlayer2() + ": " + msg);
        }
        
        System.out.println("Broadcast completed");
    }

    // Xử lý khi có người chơi tham gia phòng
    public synchronized void onPlayerJoined(Room room, BiConsumer<String, String> sendToUser) {
        if (room == null) {
            System.out.println("Room does not exist");
            return;
        }
        
        System.out.println("Checking to start game for room " + room.getId());

        // Kiểm tra có đủ 2 người chơi không
        boolean hasP1 = room.getPlayer1() != null && !room.getPlayer1().isEmpty();
        boolean hasP2 = room.getPlayer2() != null && !room.getPlayer2().isEmpty();
        
        if (!hasP1 || !hasP2) {
            System.out.println("Not enough players yet, waiting...");
            return;
        }

        // Cập nhật trạng thái phòng
        if (room.isWaiting() || "WAITING".equalsIgnoreCase(room.getStatus())) {
            room.setStatus("PLAYING");
            System.out.println("Room status updated to: PLAYING");
        }

        // Chuẩn bị thông tin game
        String roomId = room.getId();
        int boardSize = 20;  // Kích thước bàn cờ
        String turn = "X";   // X đi trước

        String p1 = room.getPlayer1(); // Người chơi X
        String p2 = room.getPlayer2(); // Người chơi O

        // Tạo tin nhắn bắt đầu game
        String msgToP1 = "GAME_STARTED|" + roomId + "|X|" + turn + "|" + boardSize;
        String msgToP2 = "GAME_STARTED|" + roomId + "|O|" + turn + "|" + boardSize;

        // Gửi tin nhắn cho cả 2 người chơi
        sendToUser.accept(p1, msgToP1);
        sendToUser.accept(p2, msgToP2);

        System.out.println("Game started!");
        System.out.println(p1 + " = X (first)");
        System.out.println(p2 + " = O");
        System.out.println("Board size: " + boardSize + "x" + boardSize);
    }

    // Tạo chuỗi CSV danh sách người chơi
    private String buildPlayersCsv(Room room) {
        System.out.println("Creating CSV of players list...");
        
        StringBuilder sb = new StringBuilder();
        if (room.getPlayer1() != null) sb.append(room.getPlayer1());
        if (room.getPlayer2() != null) {
            if (sb.length() > 0) sb.append(",");
            sb.append(room.getPlayer2());
        }
        
        String result = sb.toString();
        System.out.println("CSV: " + result);
        return result;
    }
}