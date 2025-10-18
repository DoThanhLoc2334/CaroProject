/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import Controller.SocketHandle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class HomePageFrame extends JFrame {
    private SocketHandle socketHandle;
    private String username;

    private JLabel lblWelcome;
    private JButton btnCreateRoom, btnJoinRoom, btnLogout;

    public HomePageFrame(SocketHandle socketHandle, String username) {
        this.socketHandle = socketHandle;
        this.username = username;

        initComponents();
    }

    private void initComponents() {
        setTitle("Caro - Home Page");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        lblWelcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        btnCreateRoom = new JButton("Create Room");
        btnJoinRoom = new JButton("Join Room");
        btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(lblWelcome);
        panel.add(btnCreateRoom);
        panel.add(btnJoinRoom);
        panel.add(btnLogout);

        add(panel);

        // Sự kiện
        btnCreateRoom.addActionListener(this::onCreateRoom);
        btnJoinRoom.addActionListener(this::onJoinRoom);
        btnLogout.addActionListener(this::onLogout);
    }

    // 🟢 Gửi lệnh CREATE_ROOM tới server
    private void onCreateRoom(ActionEvent e) {
        try {
            socketHandle.sendMessage("CREATE_ROOM|" + username);
            String response = socketHandle.receiveMessage();

            if (response == null) {
                JOptionPane.showMessageDialog(this, "Server not responding!");
                return;
            }

            if (response.startsWith("ROOM_CREATED")) {
                String[] parts = response.split("\\|");
                String roomId = parts[1];
                JOptionPane.showMessageDialog(this, "Room created successfully! Room ID: " + roomId);
                // TODO: chuyển sang GameFrame để chờ đối thủ
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create room: " + response);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 🔵 Gửi lệnh JOIN_ROOM tới server (demo)
    private void onJoinRoom(ActionEvent e) {
        String roomId = JOptionPane.showInputDialog(this, "Enter Room ID to join:");

        if (roomId == null || roomId.trim().isEmpty()) return;

        try {
            socketHandle.sendMessage("JOIN_ROOM|" + roomId + "|" + username);
            String response = socketHandle.receiveMessage();

            if (response == null) {
                JOptionPane.showMessageDialog(this, "No response from server!");
                return;
            }

            if (response.startsWith("ROOM_JOINED")) {
                JOptionPane.showMessageDialog(this, "Joined room successfully!");
                // TODO: mở GameFrame
            } else {
                JOptionPane.showMessageDialog(this, "Join room failed: " + response);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 🔴 Đăng xuất
    private void onLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                socketHandle.sendMessage("LOGOUT|" + username);
            } catch (Exception ex) {
                // bỏ qua
            }
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}

