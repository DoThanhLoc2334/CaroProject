/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        room.setStatus("PLAYING");
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

}

