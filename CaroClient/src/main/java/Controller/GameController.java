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


public class GameController {

    private static GameController instance;
    public static GameController getInstance() {
        if (instance == null) instance = new GameController();
        return instance;
    }
    private GameController() {}

    private HomePageFrame    homePage;
    private RoomManagerFrame currentWaitingRoom;
    private GameFrame        currentGameFrame;

    public void attachHome(HomePageFrame f) { this.homePage = f; }
    public void setCurrentWaitingRoom(RoomManagerFrame f) { this.currentWaitingRoom = f; }
    public void setCurrentGameFrame(GameFrame f) { this.currentGameFrame = f; }

    public void onRawMessage(String line) {
        if (line == null || line.isEmpty()) return;

        System.out.println("[GameController] RAW: " + line); // debug
        String[] p = line.split("\\|");
        String type = p[0];

        switch (type) {

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

            case "GAME_STARTED": {
                String roomIdStr = p.length >= 2 ? p[1] : "";
                char youAre = p.length >= 3 && !p[2].isEmpty() ? p[2].charAt(0) : 'X';
                char turn   = p.length >= 4 && !p[3].isEmpty() ? p[3].charAt(0) : 'X';
                int size    = p.length >= 5 ? parseIntSafe(p[4]) : 20;

                SwingUtilities.invokeLater(() -> {
                    GameFrame game = new GameFrame(roomIdStr, size, youAre, turn);
                    setCurrentGameFrame(game);
                    game.setVisible(true);
                    if (currentWaitingRoom != null) { currentWaitingRoom.dispose(); currentWaitingRoom = null; }
                });
                break;
            }

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

            case "GAME_OVER": {
                char winnerMark = (p.length >= 3 && !p[2].isEmpty()) ? p[2].charAt(0) : '?';
                String winnerName = p.length >= 4 ? p[3] : "";

                SwingUtilities.invokeLater(() -> {
                    if (currentGameFrame != null) {
                        currentGameFrame.gameOver(winnerMark, winnerName);
                    }
                });
                break;
            }

            case "ROOM_LIST": {
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
