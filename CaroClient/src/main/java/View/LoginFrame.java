package View;

import Controller.ClientHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private ClientHandler clientHandler;

    // ✅ Constructor mặc định (cho phép chạy nhanh để test)
    public LoginFrame() {
        this(new Controller.ClientHandler("localhost", 5000));
    }

    // ✅ Constructor có ClientHandler (được dùng khi gọi từ RegisterFrame)
    public LoginFrame(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;

        setTitle("Caro - Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        panel.add(lblUser);
        panel.add(txtUsername);
        panel.add(lblPass);
        panel.add(txtPassword);
        panel.add(btnLogin);
        panel.add(btnRegister);
        add(panel);

        // ✅ Gắn sự kiện thật (gửi dữ liệu tới server)
        btnLogin.addActionListener(this::handleLogin);
        btnRegister.addActionListener(this::handleRegister);
    }

    // Xử lý khi nhấn nút "Login"
    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        // Gửi yêu cầu đăng nhập đến server
        clientHandler.sendMessage("LOGIN|" + username + "|" + password);

        // Chờ phản hồi từ server
        String response = clientHandler.receiveMessage();
        if (response == null) {
            JOptionPane.showMessageDialog(this, "Server not responding!");
            return;
        }

        if (response.equalsIgnoreCase("LOGIN_SUCCESS")) {
            JOptionPane.showMessageDialog(this, "✅ Login successful!");
            // TODO: Chuyển sang màn hình chính của game (MainGameFrame)
        } else {
            JOptionPane.showMessageDialog(this, "❌ Login failed. Please check username/password.");
        }
    }

    // Xử lý khi nhấn nút "Register" → mở form đăng ký
    private void handleRegister(ActionEvent e) {
        this.dispose();
        new RegisterFrame(clientHandler).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
