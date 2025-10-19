/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import View.GameFrame;
import View.HomePageFrame;
import View.RoomManagerFrame;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Trung tâm điều phối message từ SocketHandle -> UI
 * B2: xử lý các gói: ROOM_CREATED, ROOM_JOINED, JOINED_ROOM, GAME_STARTED,
 *     MOVE_APPLIED, GAME_OVER, ERROR
 */
public class GameController {

    // ===== Singleton =====
    private static GameController instance;
    public static GameController getInstance() {
        if (instance == null) instance = new GameController();
        return instance;
    }
    private GameController() {}

    // ===== Tham chiếu UI =====
    private HomePageFrame    homePage;
    private RoomManagerFrame currentWaitingRoom;
    private GameFrame        currentGameFrame;

    // --- attach từ các Frame khi khởi tạo chúng ---
    public void attachHome(HomePageFrame f) { this.homePage = f; }
    public void setCurrentWaitingRoom(RoomManagerFrame f) { this.currentWaitingRoom = f; }
    public void setCurrentGameFrame(GameFrame f) { this.currentGameFrame = f; }

    // ===== Nhận raw message từ SocketHandle.startListening() =====
    public void onRawMessage(String line) {
        if (line == null || line.isEmpty()) return;

        System.out.println("[GameController] RAW: " + line); // debug
        String[] p = line.split("\\|");
        String type = p[0];

        switch (type) {

            // SERVER -> CLIENT: ROOM_CREATED|<roomId>
            case "ROOM_CREATED": {
                String roomId = p.length >= 2 ? p[1] : "";
                SwingUtilities.invokeLater(() -> {
                    RoomManagerFrame wr = new RoomManagerFrame(SocketHandle.getInstance(), roomId);
                    setCurrentWaitingRoom(wr);
                    wr.setVisible(true);
                    if (homePage != null) homePage.dispose();
                });
                break;
            }

            // SERVER -> CLIENT: ROOM_JOINED|<roomId>  (xác nhận cho người vừa join)
            case "ROOM_JOINED": {
                String roomId = p.length >= 2 ? p[1] : "";
                SwingUtilities.invokeLater(() -> {
                    if (currentWaitingRoom == null) {
                        RoomManagerFrame wr = new RoomManagerFrame(SocketHandle.getInstance(), roomId);
                        setCurrentWaitingRoom(wr);
                        wr.setVisible(true);
                        if (homePage != null) homePage.dispose();
                    }
                });
                break;
            }

            // SERVER -> CLIENT: JOINED_ROOM|<roomId>|name1,name2  (broadcast danh sách)
            case "JOINED_ROOM": {
                String roomId = p.length >= 2 ? p[1] : "";
                List<String> names = new ArrayList<>();
                if (p.length >= 3 && p[2] != null && !p[2].isEmpty()) {
                    names = Arrays.asList(p[2].split(","));
                }
                List<String> finalNames = names;
                SwingUtilities.invokeLater(() -> {
                    if (currentWaitingRoom == null) {
                        RoomManagerFrame wr = new RoomManagerFrame(SocketHandle.getInstance(), roomId);
                        setCurrentWaitingRoom(wr);
                        wr.setVisible(true);
                        if (homePage != null) homePage.dispose();
                    }
                    if (currentWaitingRoom != null) {
                        currentWaitingRoom.setPlayers(finalNames);
                    }
                });
                break;
            }

            // SERVER -> CLIENT: GAME_STARTED|<roomId>|<youAre>|<turn>|<boardSize>
            case "GAME_STARTED": {
                // roomId là CHUỖI (VD: "70749A"), KHÔNG ép int
                String roomIdStr = p.length >= 2 ? p[1] : "";
                char youAre = p.length >= 3 && !p[2].isEmpty() ? p[2].charAt(0) : 'X';
                char turn   = p.length >= 4 && !p[3].isEmpty() ? p[3].charAt(0) : 'X';
                int size    = p.length >= 5 ? parseIntSafe(p[4]) : 20;

                SwingUtilities.invokeLater(() -> {
                    // YÊU CẦU: GameFrame có constructor (String roomIdStr, int size, char youAre, char turn)
                    GameFrame game = new GameFrame(roomIdStr, size, youAre, turn);
                    setCurrentGameFrame(game);
                    game.setVisible(true);
                    if (currentWaitingRoom != null) { currentWaitingRoom.dispose(); currentWaitingRoom = null; }
                });
                break;
            }

            // SERVER -> CLIENT: MOVE_APPLIED|<roomId>|<x>|<y>|<mark>|<nextTurn>
            case "MOVE_APPLIED": {
                int x = p.length >= 3 ? parseIntSafe(p[2]) : -1;
                int y = p.length >= 4 ? parseIntSafe(p[3]) : -1;
                char mark = p.length >= 5 && !p[4].isEmpty() ? p[4].charAt(0) : 'X';
                char next = p.length >= 6 && !p[5].isEmpty() ? p[5].charAt(0) : 'X';

                SwingUtilities.invokeLater(() -> {
                    if (currentGameFrame != null) currentGameFrame.applyMove(x, y, mark, next);
                });
                break;
            }

            // SERVER -> CLIENT: GAME_OVER|<roomId>|<winnerMark>|<winnerName>
            case "GAME_OVER": {
                char winnerMark = (p.length >= 3 && !p[2].isEmpty()) ? p[2].charAt(0) : '?';
                String winnerName = p.length >= 4 ? p[3] : "";

                SwingUtilities.invokeLater(() -> {
                    if (currentGameFrame != null) {
                        currentGameFrame.gameOver(winnerMark, winnerName); // khóa bàn cờ + popup
                    }
                });
                break;
            }

            case "ROOM_LIST": {
                // parse danh sách phòng nếu cần
                break;
            }

            case "ERROR": {
                String msg = p.length >= 2 ? p[1] : "Unknown error";
                SwingUtilities.invokeLater(() ->
                        javax.swing.JOptionPane.showMessageDialog(null, msg, "Error",
                                javax.swing.JOptionPane.ERROR_MESSAGE));
                break;
            }

            default:
                System.out.println("[GameController] Unhandled message: " + line);
        }
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
}
