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

        // Cho GameController biết Home hiện tại để khi mở màn khác có thể đóng Home
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

        // Sự kiện
        btnCreateRoom.addActionListener(this::onCreateRoom);
        btnJoinRoom.addActionListener(this::onJoinRoom);
        btnLogout.addActionListener(this::onLogout);
    }

    // 🟢 Chỉ GỬI lệnh CREATE_ROOM; phản hồi sẽ do listener + GameController xử lý
    private void onCreateRoom(ActionEvent e) {
        socketHandle.sendMessage("CREATE_ROOM|" + username);
        // Tuỳ chọn: báo đang chờ server phản hồi
        JOptionPane.showMessageDialog(this, "Creating room...\nPlease wait for server response.",
                "Create Room", JOptionPane.INFORMATION_MESSAGE);
        // Khi server trả "ROOM_CREATED|<id>", GameController sẽ mở RoomManagerFrame.
    }

    // 🔵 Chỉ GỬI lệnh JOIN_ROOM; phản hồi do GameController xử lý
    private void onJoinRoom(ActionEvent e) {
        String roomId = JOptionPane.showInputDialog(this, "Enter Room ID to join:");
        if (roomId == null || roomId.trim().isEmpty()) return;

        socketHandle.sendMessage("JOIN_ROOM|" + roomId.trim() + "|" + username);
        // Khi server trả "ROOM_JOINED"/"JOINED_ROOM"/"GAME_STARTED", GameController sẽ mở màn tương ứng.
    }

    // 🔴 Đăng xuất
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
