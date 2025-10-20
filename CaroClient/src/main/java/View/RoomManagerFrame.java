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
    private JButton btnJoinRoom, btnRefresh, btnBack;

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

        btnJoinRoom = new JButton("Join Room");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        buttonPanel.add(btnJoinRoom);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnJoinRoom.addActionListener(this::onJoinRoom);
        btnRefresh.addActionListener(this::onRefresh);
        btnBack.addActionListener(this::onBack);
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

    public void updateRoomList(ArrayList<String> rooms) {
        roomListModel.clear();
        for (String room : rooms) {
            roomListModel.addElement(room);
        }
    }

    public void setPlayers(java.util.List<String> finalNames) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
