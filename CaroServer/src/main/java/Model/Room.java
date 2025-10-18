/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author doloc
 */

public class Room {
    private String id;
    private String player1;
    private String player2;
    private String status; // WAITING, PLAYING, FINISHED

    // Constructor tạo phòng mới (chỉ có người chơi 1)
    public Room(String id, String player1) {
        this.id = id;
        this.player1 = player1;
        this.player2 = null;
        this.status = "WAITING";
    }

    // Constructor đầy đủ (phòng đã có 2 người)
    public Room(String id, String player1, String player2, String status) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.status = status;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Kiểm tra phòng đã đủ 2 người hay chưa
    public boolean isFull() {
        return player1 != null && player2 != null;
    }

    // Kiểm tra phòng đang chơi hay chờ
    public boolean isWaiting() {
        return "WAITING".equalsIgnoreCase(status);
    }

    public boolean isPlaying() {
        return "PLAYING".equalsIgnoreCase(status);
    }

    public boolean isFinished() {
        return "FINISHED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

