package Controller;

import Model.UserManager;
import Model.User;
import Model.RoomManager;
import Model.Room;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserManager userManager;
    private final RoomManager roomManager;
    private BufferedReader in;
    private PrintWriter out;
    private String currentUsername;

    public ClientHandler(Socket socket, UserManager userManager, RoomManager roomManager) {
        this.socket = socket;
        this.userManager = userManager;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
            }

        } catch (IOException e) {
            System.out.println("[ClientHandler] Connection closed: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    // ======================== XỬ LÝ CÁC LỆNH ========================
    private void handleMessage(String msg) {
        System.out.println("[Server] Received: " + msg);

        // ====== ĐĂNG KÝ ======
        if (msg.startsWith("REGISTER|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];

                boolean success = userManager.register(username, password);
                if (success) {
                    out.println("REGISTER_SUCCESS");
                    System.out.println("[Server] User registered: " + username);
                } else {
                    out.println("REGISTER_FAIL");
                    System.out.println("[Server] Register failed for: " + username);
                }
            } else {
                out.println("REGISTER_FAIL|INVALID_FORMAT");
            }

        // ====== ĐĂNG NHẬP ======
        } else if (msg.startsWith("LOGIN|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];

                User user = userManager.login(username, password);
                if (user != null) {
                    currentUsername = username;
                    out.println("LOGIN_SUCCESS");
                    System.out.println("[Server] Login success: " + username);
                } else {
                    out.println("LOGIN_FAIL");
                    System.out.println("[Server] Login failed: " + username);
                }
            } else {
                out.println("LOGIN_FAIL|INVALID_FORMAT");
            }

        // ====== TẠO PHÒNG ======
        } else if (msg.startsWith("CREATE_ROOM")) {
            if (currentUsername == null) {
                out.println("ERROR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = msg.split("\\|");
            String creator = currentUsername;
            if (parts.length > 1 && !parts[1].isBlank()) {
                creator = parts[1];
            }

            Room room = roomManager.createRoom(creator);
            if (room != null) {
                // ✅ Format khớp với client
                out.println("ROOM_CREATED|" + room.getId());
                System.out.println("[Server] " + creator + " created room " + room.getId());
            } else {
                out.println("ERROR|CREATE_ROOM_FAILED");
                System.out.println("[Server] Failed to create room for " + creator);
            }

        // ====== THAM GIA PHÒNG ======
        } else if (msg.startsWith("JOIN_ROOM|")) {
            if (currentUsername == null) {
                out.println("ERROR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = msg.split("\\|");
            if (parts.length < 3) { // JOIN_ROOM|roomId|username
                out.println("ERROR|INVALID_FORMAT");
                return;
            }

            String roomId = parts[1];
            String joiner = parts[2];
            Room room = roomManager.joinRoom(roomId, joiner);

            if (room != null) {
                // ✅ Format khớp với client HomePageFrame
                out.println("ROOM_JOINED|" + room.getId());
                System.out.println("[Server] " + joiner + " joined room " + roomId);
            } else {
                out.println("ERROR|JOIN_ROOM_FAILED|" + roomId);
                System.out.println("[Server] Join room failed: " + roomId);
            }

        // ====== DANH SÁCH PHÒNG CHỜ ======
        } else if (msg.equals("LIST_ROOMS")) {
            List<Room> waitingRooms = roomManager.getWaitingRooms();
            if (waitingRooms.isEmpty()) {
                out.println("ROOM_LIST|EMPTY");
            } else {
                StringBuilder sb = new StringBuilder("ROOM_LIST");
                for (Room room : waitingRooms) {
                    sb.append("|")
                      .append(room.getId())
                      .append(",")
                      .append(room.getPlayer1())
                      .append(",")
                      .append(room.getStatus());
                }
                out.println(sb.toString());
            }

        // ====== LỆNH KHÔNG HỢP LỆ ======
        } else {
            out.println("UNKNOWN_COMMAND");
            System.out.println("[Server] Unknown command: " + msg);
        }
    }
}
