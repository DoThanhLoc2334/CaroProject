/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Arrays;


public class Room {
    private String id;
    private String player1;   
    private String player2;   
    private String status;    

    private final int size = 20;         
    private final char[][] board;        
    private char turn = 'X';             

    public Room(String id, String player1) {
        this.id = id;
        this.player1 = player1;
        this.player2 = null;
        this.status = "WAITING";
        this.board = new char[size][size];
        for (char[] row : board) Arrays.fill(row, '\0');
    }

    public Room(String id, String player1, String player2, String status) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.status = status;
        this.board = new char[size][size];
        for (char[] row : board) Arrays.fill(row, '\0');
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlayer1() { return player1; }
    public void setPlayer1(String player1) { this.player1 = player1; }

    public String getPlayer2() { return player2; }
    public void setPlayer2(String player2) { this.player2 = player2; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isFull() { return player1 != null && player2 != null; }
    public boolean isWaiting() { return "WAITING".equalsIgnoreCase(status) && player2 == null; }
    public boolean isPlaying() { return "PLAYING".equalsIgnoreCase(status); }
    public boolean isFinished() { return "FINISHED".equalsIgnoreCase(status); }

    public int getBoardSize() { return size; }

    public synchronized char getTurn() { return turn; }

    public synchronized void setTurn(char turn) {
        if (turn == 'X' || turn == 'O') this.turn = turn;
    }

    
    public synchronized boolean placeMark(int x, int y, char mark) {
        if (mark != 'X' && mark != 'O') return false;
        if (x < 0 || y < 0 || x >= size || y >= size) return false;
        if (board[x][y] != '\0') return false;
        board[x][y] = mark;
        return true;
    }

   
    public synchronized boolean checkWin(int x, int y, char mark) {
        if (mark != 'X' && mark != 'O') return false;
        return countLine(x, y, 1, 0, mark) + countLine(x, y, -1, 0, mark) - 1 >= 5 || // ngang
               countLine(x, y, 0, 1, mark) + countLine(x, y, 0, -1, mark) - 1 >= 5 || // dọc
               countLine(x, y, 1, 1, mark) + countLine(x, y, -1, -1, mark) - 1 >= 5 || // chéo \
               countLine(x, y, 1, -1, mark) + countLine(x, y, -1, 1, mark) - 1 >= 5;   // chéo /
    }

    private int countLine(int x, int y, int dx, int dy, char mark) {
        int cnt = 0, i = x, j = y;
        while (i >= 0 && j >= 0 && i < size && j < size && board[i][j] == mark) {
            cnt++; i += dx; j += dy;
        }
        return cnt;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", status='" + status + '\'' +
                ", turn=" + turn +
                '}';
    }
}
