/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.SocketHandle;
import Model.XOButton;

import javax.swing.*;
import java.awt.*;


public class GameFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GameFrame.class.getName());

    private String roomIdStr = "";
    private int boardSize = 20;
    private char me = 'X';
    private char turn = 'X';
    private boolean finished = false;

    private XOButton[][] buttons;

    public GameFrame() {
        initComponents();
        setLocationRelativeTo(null);
        setupBoard(false);
    }

    public GameFrame(int roomId, int size, char youAre, char startTurn) {
        this(String.valueOf(roomId), size, youAre, startTurn);
    }

    public GameFrame(String roomIdStr, int size, char youAre, char startTurn) {
        initComponents();
        setLocationRelativeTo(null);

        this.roomIdStr  = roomIdStr != null ? roomIdStr : "";
        this.boardSize  = size > 0 ? size : 20;
        this.me         = youAre;
        this.turn       = startTurn;

        lblStatus.setText(statusText());
        setTitle("Caro - Room " + this.roomIdStr + " | You: " + this.me);

        setupBoard(true);
    }

    private void setupBoard(boolean enableSendMove) {
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
                        if (finished) return;
                        if (!btn.isEmpty()) return;
                        if (turn != me) return;

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

        lblStatus.setText(statusText());
        pack();
    }

    private String statusText() {
        return (finished ? "Finished • " : "") + "You are " + me + " | Turn: " + turn;
    }

    public void disableBoard() {
        if (buttons == null) return;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }


    public void applyMove(int x, int y, char mark, char nextTurn) {
        if (buttons == null || finished) return;
        if (x < 0 || y < 0 || x >= boardSize || y >= boardSize) return;

        buttons[x][y].setMark(mark);
        this.turn = nextTurn;
        lblStatus.setText(statusText());
    }


    public void gameOver(char winnerMark, String winnerName) {
        finished = true;
        disableBoard();
        lblStatus.setText(statusText());

        String msg = (winnerMark == me)
                ? "🎉 Bạn đã THẮNG!\nNgười thắng: " + winnerName + " (" + winnerMark + ")"
                : "😢 Bạn đã THUA.\nNgười thắng: " + winnerName + " (" + winnerMark + ")";
        JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);

    }

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

    private javax.swing.JButton btnQuitGame;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel panelBoard;
}
