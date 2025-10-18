/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;
// RoomManagerFrame.java

import Controller.SocketHandle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RoomManagerFrame extends JFrame {
    private SocketHandle socketHandle;
    private String username;
    private DefaultListModel<String> roomListModel;
    private JList<String> roomList;
    private JButton btnCreateRoom, btnJoinRoom, btnRefresh, btnBack;

    public RoomManagerFrame(SocketHandle socketHandle, String username) {
        this.socketHandle = socketHandle;
        this.username = username;

        initUI();
        setTitle("Room Manager - " + username);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        JScrollPane scrollPane = new JScrollPane(roomList);

        btnCreateRoom = new JButton("Create Room");
        btnJoinRoom = new JButton("Join Room");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        buttonPanel.add(btnCreateRoom);
        buttonPanel.add(btnJoinRoom);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Gắn sự kiện
        btnCreateRoom.addActionListener(this::onCreateRoom);
        btnJoinRoom.addActionListener(this::onJoinRoom);
        btnRefresh.addActionListener(this::onRefresh);
        btnBack.addActionListener(this::onBack);
    }

    private void onCreateRoom(ActionEvent e) {
        String roomName = JOptionPane.showInputDialog(this, "Enter room name:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            socketHandle.sendMessage("CREATE_ROOM|" + roomName + "|" + username);
        }
    }

    private void onJoinRoom(ActionEvent e) {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom != null) {
            socketHandle.sendMessage("JOIN_ROOM|" + selectedRoom + "|" + username);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a room to join!");
        }
    }

    private void onRefresh(ActionEvent e) {
        socketHandle.sendMessage("GET_ROOMS|");
    }

    private void onBack(ActionEvent e) {
        dispose();
        new HomePageFrame(socketHandle, username).setVisible(true);
    }

    // Cập nhật danh sách phòng từ server
    public void updateRoomList(ArrayList<String> rooms) {
        roomListModel.clear();
        for (String room : rooms) {
            roomListModel.addElement(room);
        }
    }
}

