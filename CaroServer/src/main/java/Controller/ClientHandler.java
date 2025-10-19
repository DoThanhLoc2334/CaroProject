package Controller;

import Model.UserManager;
import Model.User;
import Model.RoomManager;
import Model.Room;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {

    // ==== Quản lý kết nối online theo username để gửi targeted messages ====
    private static final ConcurrentHashMap<String, ClientHandler> ONLINE = new ConcurrentHashMap<>();

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
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
            }

        } catch (IOException e) {
            System.out.println("[ClientHandler] Connection closed: " + socket.getInetAddress());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            // remove khỏi ONLINE nếu đang đăng nhập
            if (currentUsername != null) {
                ONLINE.remove(currentUsername);
                System.out.println("[Server] Offline: " + currentUsername);
            }
        }
    }

    // Gửi message cho client của chính handler này
    private void send(String msg) {
        out.println(msg);
    }

    // ===== Gửi tới user bất kỳ qua bảng ONLINE (dùng cho broadcast) =====
    private void sendToUser(String username, String msg) {
        ClientHandler h = ONLINE.get(username);
        if (h != null) {
            h.send(msg);
        } else {
            System.out.println("[Server] Cannot send to " + username + " (offline?) -> " + msg);
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
                    send("REGISTER_SUCCESS");
                    System.out.println("[Server] User registered: " + username);
                } else {
                    send("REGISTER_FAIL");
                    System.out.println("[Server] Register failed for: " + username);
                }
            } else {
                send("REGISTER_FAIL|INVALID_FORMAT");
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
                    ONLINE.put(username, this); // <== thêm vào bảng online
                    send("LOGIN_SUCCESS");
                    System.out.println("[Server] Login success: " + username);
                } else {
                    send("LOGIN_FAIL");
                    System.out.println("[Server] Login failed: " + username);
                }
            } else {
                send("LOGIN_FAIL|INVALID_FORMAT");
            }

        // ====== TẠO PHÒNG ======
        } else if (msg.startsWith("CREATE_ROOM")) {
            if (currentUsername == null) {
                send("ERROR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = msg.split("\\|");
            String creator = currentUsername;
            if (parts.length > 1 && !parts[1].isBlank()) {
                creator = parts[1];
            }

            Room room = roomManager.createRoom(creator);
            if (room != null) {
                send("ROOM_CREATED|" + room.getId());
                System.out.println("[Server] " + creator + " created room " + room.getId());
            } else {
                send("ERROR|CREATE_ROOM_FAILED");
                System.out.println("[Server] Failed to create room for " + creator);
            }

        // ====== THAM GIA PHÒNG ======
        } else if (msg.startsWith("JOIN_ROOM|")) {
            if (currentUsername == null) {
                send("ERROR|NOT_LOGGED_IN");
                return;
            }

            String[] parts = msg.split("\\|");
            if (parts.length < 3) { // JOIN_ROOM|roomId|username
                send("ERROR|INVALID_FORMAT");
                return;
            }

            String roomId = parts[1];
            String joiner = parts[2];

            Room room = roomManager.joinRoom(roomId, joiner);
            if (room != null) {
                // 1) xác nhận cho client vừa join
                send("ROOM_JOINED|" + room.getId());
                System.out.println("[Server] " + joiner + " joined room " + roomId);

                // 2) broadcast danh sách người trong phòng (JOINED_ROOM|id|name1,name2)
                roomManager.broadcastPlayers(room, this::sendToUser);

                // 3) nếu đủ 2 người → auto-start (GAME_STARTED|roomId|youAre|turn|boardSize)
                roomManager.onPlayerJoined(room, this::sendToUser);

            } else {
                send("ERROR|JOIN_ROOM_FAILED|" + roomId);
                System.out.println("[Server] Join room failed: " + roomId);
            }

        // ====== DANH SÁCH PHÒNG CHỜ ======
        } else if (msg.equals("LIST_ROOMS")) {
            List<Room> waitingRooms = roomManager.getWaitingRooms();
            if (waitingRooms.isEmpty()) {
                send("ROOM_LIST|EMPTY");
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
                send(sb.toString());
            }

        // ====== ĐÁNH NƯỚC: MOVE|<roomId>|<x>|<y> ======
        } else if (msg.startsWith("MOVE|")) {
            String[] parts = msg.split("\\|");
            if (parts.length < 4) { send("ERROR|INVALID_MOVE_FORMAT"); return; }

            String roomId = parts[1];   // LƯU Ý: roomId là CHUỖI (VD: 70749A)
            int x, y;
            try {
                x = Integer.parseInt(parts[2]);
                y = Integer.parseInt(parts[3]);
            } catch (NumberFormatException nfe) {
                send("ERROR|INVALID_COORD");
                return;
            }

            Room room = roomManager.getRoomById(roomId);
            if (room == null) {
                send("ERROR|ROOM_NOT_FOUND|" + roomId);
                return;
            }
            if (!room.isFull() || !room.isPlaying()) {
                send("ERROR|ROOM_NOT_PLAYING");
                return;
            }

            // Xác định người chơi hiện tại là X hay O
            char myMark;
            if (currentUsername != null && currentUsername.equals(room.getPlayer1())) {
                myMark = 'X';
            } else if (currentUsername != null && currentUsername.equals(room.getPlayer2())) {
                myMark = 'O';
            } else {
                send("ERROR|NOT_IN_ROOM");
                return;
            }

            // Kiểm tra lượt
            if (room.getTurn() != myMark) {
                send("ERROR|NOT_YOUR_TURN");
                return;
            }

            // Đặt quân nếu ô trống
            boolean placed = room.placeMark(x, y, myMark);
            if (!placed) {
                send("ERROR|CELL_TAKEN");
                return;
            }

            // Đổi lượt
            char nextTurn = (myMark == 'X') ? 'O' : 'X';
            room.setTurn(nextTurn);

            // Broadcast nước đi
            String applied = "MOVE_APPLIED|" + roomId + "|" + x + "|" + y + "|" + myMark + "|" + nextTurn;
            sendToUser(room.getPlayer1(), applied);
            sendToUser(room.getPlayer2(), applied);

            // ====== THẮNG CUỘC: gửi GAME_OVER ngay lập tức ======
            if (room.checkWin(x, y, myMark)) {
                String winnerName = (myMark == 'X') ? room.getPlayer1() : room.getPlayer2();
                String over = "GAME_OVER|" + roomId + "|" + myMark + "|" + winnerName;
                sendToUser(room.getPlayer1(), over);
                sendToUser(room.getPlayer2(), over);
                room.setStatus("FINISHED");
            }

        // ====== ĐĂNG XUẤT ======
        } else if (msg.startsWith("LOGOUT|")) {
            String[] parts = msg.split("\\|");
            String user = (parts.length >= 2) ? parts[1] : currentUsername;
            if (user != null) {
                ONLINE.remove(user);
                System.out.println("[Server] Logout: " + user);
            }
            send("LOGOUT_OK");

        // ====== LỆNH KHÔNG HỢP LỆ ======
        } else {
            send("UNKNOWN_COMMAND");
            System.out.println("[Server] Unknown command: " + msg);
        }
    }
}
