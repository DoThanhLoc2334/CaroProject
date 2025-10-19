// Model/GameRoom.java
package Model;

import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    public enum Status { WAITING, PLAYING, FINISHED }

    private int roomId;
    private String roomName;
    private final List<User> players = new ArrayList<>(2);
    private Status status = Status.WAITING;

    // Trạng thái ván
    private char currentTurn = 'X'; // X đi trước
    private int boardSize = 20;
    private Board board; // bạn đã có Board.java

    public GameRoom(int id, String name) {
        this.roomId = id; this.roomName = name;
        this.board = new Board(boardSize);
    }

    public int getRoomId() { return roomId; }
    public Status getStatus() { return status; }
    public List<User> getPlayers() { return players; }
    public boolean isFull() { return players.size() >= 2; }

    public void addPlayer(User u) { if (!isFull()) players.add(u); }

    public void startGame() {
        if (players.size() == 2 && status == Status.WAITING) {
            status = Status.PLAYING;
            currentTurn = 'X'; // người 1 là X, người 2 là O
            board.clear();     // nếu bạn có clear()
        }
    }

    public char getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(char c) { currentTurn = c; }
    public int getBoardSize() { return boardSize; }
    public Board getBoard() { return board; }
}
