package View;

import Controller.GameController;
import Controller.SocketHandle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HomePageFrame extends JFrame {
    private final SocketHandle socketHandle;
    private final String username;

    private JLabel lblWelcome;
    private JButton btnCreateRoom, btnJoinRoom, btnLogout;

    public HomePageFrame(SocketHandle socketHandle, String username) {
        this.socketHandle = socketHandle;
        this.username = username;
        initComponents();

        GameController.getInstance().attachHome(this);
    }

    private void initComponents() {
        setTitle("Caro - Home Page");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        lblWelcome = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        btnCreateRoom = new JButton("Create Room");
        btnJoinRoom   = new JButton("Join Room");
        btnLogout     = new JButton("Logout");

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.add(lblWelcome);
        panel.add(btnCreateRoom);
        panel.add(btnJoinRoom);
        panel.add(btnLogout);
        add(panel);

        btnCreateRoom.addActionListener(this::onCreateRoom);
        btnJoinRoom.addActionListener(this::onJoinRoom);
        btnLogout.addActionListener(this::onLogout);
    }

    private void onCreateRoom(ActionEvent e) {
        socketHandle.sendMessage("CREATE_ROOM|" + username);
        JOptionPane.showMessageDialog(this, "Creating room...\nPlease wait for server response.",
                "Create Room", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onJoinRoom(ActionEvent e) {
        String roomId = JOptionPane.showInputDialog(this, "Enter Room ID to join:");
        if (roomId == null || roomId.trim().isEmpty()) return;

        socketHandle.sendMessage("JOIN_ROOM|" + roomId.trim() + "|" + username);
    }

    private void onLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                socketHandle.sendMessage("LOGOUT|" + username);
            } catch (Exception ignored) {}
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
