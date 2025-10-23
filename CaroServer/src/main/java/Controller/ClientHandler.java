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

//    private static final ConcurrentHashMap<String, ClientHandler> ONLINE = new ConcurrentHashMap<>();
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private UserManager userManager;
    private RoomManager roomManager;
    private String currentUsername;
    
    // Danh sách tất cả client đang online (dùng chung giữa các ClientHandler)
    private static final ConcurrentHashMap<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public ClientHandler(Socket socket, UserManager userManager, RoomManager roomManager) {
        this.clientSocket = socket;
        this.userManager = userManager;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Connected to client: " + clientSocket.getInetAddress());
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received message: " + message);
                handleMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
            
            // Xóa khỏi danh sách online
            if (currentUsername != null) {
                onlineUsers.remove(currentUsername);
                System.out.println("User offline: " + currentUsername);
            }
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Sent message: " + message);
        }
    }

    private void sendMessageToUser(String username, String message) {
        ClientHandler handler = onlineUsers.get(username);
        if (handler != null) {
            handler.sendMessage(message);
            System.out.println("Sent message to " + username + ": " + message);
        } else {
            System.out.println("Cannot send message to " + username + " (user not online)");
        }
    }

    private void handleMessage(String message) {

        if (message.startsWith("REGISTER|")) {
            handleRegister(message);
        } else if (message.startsWith("LOGIN|")) {
            handleLogin(message);
        } else if (message.startsWith("CREATE_ROOM")) {
            handleCreateRoom(message);
        } else if (message.startsWith("JOIN_ROOM|")) {
            handleJoinRoom(message);
        } else if (message.equals("LIST_ROOMS")) {
            handleListRooms();
        } else if (message.startsWith("MOVE|")) {
            handleMove(message);
        } else if (message.startsWith("LOGOUT|")) {
            handleLogout(message);
        } else {
            // Lệnh không hợp lệ
            sendMessage("UNKNOWN_COMMAND");
            System.out.println("Lệnh không hợp lệ: " + message);
        }
    }

    private void handleRegister(String message) {
        // Tách thông tin từ tin nhắn (định dạng: REGISTER|username|password)
        String[] parts = message.split("\\|");
        // Kiểm tra định dạng tin nhắn
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            boolean success = userManager.register(username, password);
            if (success) {
                sendMessage("REGISTER_SUCCESS");
            } else {
                sendMessage("REGISTER_FAIL");
            }
        } else {
            sendMessage("REGISTER_FAIL|INVALID_FORMAT");
        }
    }

    private void handleLogin(String message) {        
        String[] parts = message.split("\\|");
        // Kiểm tra định dạng tin nhắn
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            User user = userManager.login(username, password);
            if (user != null) {
                // Đăng nhập thành công
                currentUsername = username;
                onlineUsers.put(username, this);
                sendMessage("LOGIN_SUCCESS");
            } else {
                // Đăng nhập thất bại
                sendMessage("LOGIN_FAIL");
            }
        } else {
            sendMessage("LOGIN_FAIL|INVALID_FORMAT");
        }
    }

    private void handleCreateRoom(String message) {        
        // Kiểm tra xem user đã đăng nhập chưa
        if (currentUsername == null) {
            sendMessage("ERROR|NOT_LOGGED_IN");
            return;
        }
        // Tạo phòng mới
        Room room = roomManager.createRoom(currentUsername);
        
        if (room != null) {
            sendMessage("ROOM_CREATED|" + room.getId());
        } else {
            sendMessage("ERROR|CREATE_ROOM_FAILED");
        }
    }

    private void handleJoinRoom(String message) {        
        // Kiểm tra xem user đã đăng nhập chưa
        if (currentUsername == null) {
            sendMessage("ERROR|NOT_LOGGED_IN");
            System.out.println("User not logged in");
            return;
        }

        // Tách thông tin từ tin nhắn (định dạng: JOIN_ROOM|roomId|joiner)
        String[] parts = message.split("\\|");
        if (parts.length < 3) {
            sendMessage("ERROR|INVALID_FORMAT");
            System.out.println("Invalid message format");
            return;
        }

        String roomId = parts[1];
        String joiner = parts[2];

        // Thử tham gia phòng
        Room room = roomManager.joinRoom(roomId, joiner);
        
        if (room != null) {
            sendMessage("ROOM_JOINED|" + room.getId());
            System.out.println(joiner + " joined room " + roomId);

            // Thông báo cho các player khác
            roomManager.broadcastPlayers(room, this::sendMessageToUser);
            roomManager.onPlayerJoined(room, this::sendMessageToUser);
        } else {
            sendMessage("ERROR|JOIN_ROOM_FAILED|" + roomId);
            System.out.println("Room joining failed: " + roomId);
        }
    }

    private void handleListRooms() {        
        // Lấy danh sách phòng đang chờ
        List<Room> waitingRooms = roomManager.getWaitingRooms();
        
        if (waitingRooms.isEmpty()) {
            sendMessage("ROOM_LIST|EMPTY");
            System.out.println("No rooms are waiting");
        } else {
            // Tạo chuỗi danh sách phòng
            StringBuilder roomList = new StringBuilder("ROOM_LIST");
            for (Room room : waitingRooms) {
                roomList.append("|")
                       .append(room.getId())
                       .append(",")
                       .append(room.getPlayer1())
                       .append(",")
                       .append(room.getStatus());
            }
            sendMessage(roomList.toString());
            System.out.println("Sent list of " + waitingRooms.size() + " rooms");
        }
    }

    private void handleMove(String message) {        
        // Tách thông tin từ tin nhắn (định dạng: MOVE|roomId|x|y)
        String[] parts = message.split("\\|");
        if (parts.length < 4) {
            sendMessage("ERROR|INVALID_MOVE_FORMAT");
            System.out.println("Invalid move format");
            return;
        }

        String roomId = parts[1];
        int x, y;
        
        // Chuyển đổi tọa độ từ string sang int
        try {
            x = Integer.parseInt(parts[2]);
            y = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            sendMessage("ERROR|INVALID_COORD");
            System.out.println("Invalid coordinates");
            return;
        }

        // Lấy phòng chơi
        Room room = roomManager.getRoomById(roomId);
        if (room == null) {
            sendMessage("ERROR|ROOM_NOT_FOUND|" + roomId);
            System.out.println("Room not found: " + roomId);
            return;
        }
        
        // Kiểm tra phòng có đang chơi không
        if (!room.isFull() || !room.isPlaying()) {
            sendMessage("ERROR|ROOM_NOT_PLAYING");
            System.out.println("Room not ready to play");
            return;
        }

        // Xác định ký hiệu của người chơi
        char myMark;
        if (currentUsername != null && currentUsername.equals(room.getPlayer1())) {
            myMark = 'X';
        } else if (currentUsername != null && currentUsername.equals(room.getPlayer2())) {
            myMark = 'O';
        } else {
            sendMessage("ERROR|NOT_IN_ROOM");
            System.out.println("You are not in this room");
            return;
        }

        // Kiểm tra lượt chơi
        if (room.getTurn() != myMark) {
            sendMessage("ERROR|NOT_YOUR_TURN");
            System.out.println("It's not your turn");
            return;
        }

        // Thực hiện nước đi
        boolean placed = room.placeMark(x, y, myMark);
        if (!placed) {
            sendMessage("ERROR|CELL_TAKEN");
            System.out.println("Cell already taken");
            return;
        }

        // Chuyển lượt chơi
        char nextTurn = (myMark == 'X') ? 'O' : 'X';
        room.setTurn(nextTurn);

        // Gửi thông báo nước đi cho cả hai người chơi
        String moveMessage = "MOVE_APPLIED|" + roomId + "|" + x + "|" + y + "|" + myMark + "|" + nextTurn;
        sendMessageToUser(room.getPlayer1(), moveMessage);
        sendMessageToUser(room.getPlayer2(), moveMessage);

        // Kiểm tra thắng thua
        if (room.checkWin(x, y, myMark)) {
            String winnerName = (myMark == 'X') ? room.getPlayer1() : room.getPlayer2();
            String gameOverMessage = "GAME_OVER|" + roomId + "|" + myMark + "|" + winnerName;
            sendMessageToUser(room.getPlayer1(), gameOverMessage);
            sendMessageToUser(room.getPlayer2(), gameOverMessage);
            room.setStatus("FINISHED");
            System.out.println("🏆 " + winnerName + " thắng cuộc!");
        }
    }

    private void handleLogout(String message) {
        System.out.println("👋 Xử lý đăng xuất...");
        
        // Tách thông tin từ tin nhắn
        String[] parts = message.split("\\|");
        String user = (parts.length >= 2) ? parts[1] : currentUsername;
        
        // Xóa khỏi danh sách online
        if (user != null) {
            onlineUsers.remove(user);
            System.out.println("👋 User đăng xuất: " + user);
        }
        
        sendMessage("LOGOUT_OK");
    }
}
