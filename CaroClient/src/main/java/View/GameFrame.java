/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.SocketHandle;
import Model.XOButton;

import javax.swing.*;
import java.awt.*;

/**
 * Màn hình ván chơi
 * - Dùng roomId dạng String (khớp server gửi, ví dụ "70749A")
 * - Gửi MOVE khi tới lượt mình
 * - Nhận cập nhật bằng applyMove(...) và GAME_OVER(...)
 */
public class GameFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GameFrame.class.getName());

    // Trạng thái ván
    private String roomIdStr = "";   // dùng String thay cho int
    private int boardSize = 20;
    private char me = 'X';
    private char turn = 'X';
    private boolean finished = false;

    // Bàn cờ
    private XOButton[][] buttons;

    /** Constructor mặc định cho preview GUI Builder */
    public GameFrame() {
        initComponents();
        setLocationRelativeTo(null);
        setupBoard(false); // preview, không gửi move
    }

    /** Constructor cũ (int) — giữ để tương thích, nhưng map sang String */
    public GameFrame(int roomId, int size, char youAre, char startTurn) {
        this(String.valueOf(roomId), size, youAre, startTurn);
    }

    /** Constructor mới: dùng roomId dạng String (KHỚP GAME_STARTED từ server) */
    public GameFrame(String roomIdStr, int size, char youAre, char startTurn) {
        initComponents();
        setLocationRelativeTo(null);

        this.roomIdStr  = roomIdStr != null ? roomIdStr : "";
        this.boardSize  = size > 0 ? size : 20;
        this.me         = youAre;
        this.turn       = startTurn;

        lblStatus.setText(statusText());
        setTitle("Caro - Room " + this.roomIdStr + " | You: " + this.me);

        // Khởi tạo bàn cờ có gắn logic gửi MOVE
        setupBoard(true);
    }

    // Tạo bàn cờ và gắn listener
    private void setupBoard(boolean enableSendMove) {
        // Làm sạch panel và set layout đúng kích thước hiện tại
        panelBoard.removeAll();
        panelBoard.setLayout(new GridLayout(boardSize, boardSize));
        panelBoard.setPreferredSize(new Dimension(34 * boardSize, 34 * boardSize));

        buttons = new XOButton[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                XOButton btn = new XOButton(i, j);
                btn.setPreferredSize(new Dimension(32, 32));

                if (enableSendMove) {
                    final int x = i, y = j;
                    btn.addActionListener(e -> {
                        if (finished) return;      // ván đã kết thúc
                        // Chỉ cho đánh nếu ô trống và đúng lượt
                        if (!btn.isEmpty()) return;
                        if (turn != me) return;

                        // Gửi MOVE|<roomIdStr>|x|y  (roomId dạng STRING)
                        try {
                            System.out.println("[GameFrame] SEND MOVE: " + roomIdStr + " -> (" + x + "," + y + ")");
                            SocketHandle.getInstance()
                                    .sendMessage("MOVE|" + roomIdStr + "|" + x + "|" + y);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Cannot send move: " + ex.getMessage(),
                                    "Network Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }

                panelBoard.add(btn);
                buttons[i][j] = btn;
            }
        }

        panelBoard.revalidate();
        panelBoard.repaint();

        // Cập nhật nhãn trạng thái
        lblStatus.setText(statusText());
        pack(); // tính lại kích thước theo preferred size
    }

    // Hiển thị trạng thái lượt
    private String statusText() {
        return (finished ? "Finished • " : "") + "You are " + me + " | Turn: " + turn;
    }

    /** Khóa toàn bộ bàn cờ (sau khi GAME_OVER) */
    public void disableBoard() {
        if (buttons == null) return;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    /**
     * Được GameController gọi khi server phát MOVE_APPLIED|roomId|x|y|mark|nextTurn
     */
    public void applyMove(int x, int y, char mark, char nextTurn) {
        if (buttons == null || finished) return;
        if (x < 0 || y < 0 || x >= boardSize || y >= boardSize) return;

        buttons[x][y].setMark(mark);   // vẽ X/O
        this.turn = nextTurn;
        lblStatus.setText(statusText());
    }

    /**
     * Được GameController gọi khi server phát GAME_OVER|roomId|winnerMark|winnerName
     */
    public void gameOver(char winnerMark, String winnerName) {
        finished = true;
        disableBoard();
        lblStatus.setText(statusText());

        String msg = (winnerMark == me)
                ? "🎉 Bạn đã THẮNG!\nNgười thắng: " + winnerName + " (" + winnerMark + ")"
                : "😢 Bạn đã THUA.\nNgười thắng: " + winnerName + " (" + winnerMark + ")";
        JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        // Nếu muốn đóng cửa sổ ngay:
        // dispose();
    }

    // ================== Code sinh bởi GUI Builder ==================
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        panelBoard = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        btnQuitGame = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelBoard.setLayout(new java.awt.GridLayout(20, 20));

        lblStatus.setText("You are X | Turn: X");

        btnQuitGame.setText("Thoát ván");
        btnQuitGame.addActionListener(e -> {
            this.dispose();
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(panelBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQuitGame, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(panelBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnQuitGame, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>                        

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new GameFrame().setVisible(true));
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnQuitGame;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelBoard;
    // End of variables declaration
}
