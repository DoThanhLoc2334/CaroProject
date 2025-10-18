package View;

import Controller.SocketHandle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private SocketHandle socketHandle;

    // ✅ Constructor mặc định (dùng khi chạy độc lập)
    public LoginFrame() {
        try {
            this.socketHandle = new SocketHandle("localhost", 5000);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Cannot connect to server!");
            return;
        }
        initComponents();
    }

    // ✅ Constructor dùng chung socket (khi mở từ RegisterFrame)
    public LoginFrame(SocketHandle socketHandle) {
        this.socketHandle = socketHandle;
        initComponents();
    }

    private void initComponents() {
        setTitle("Caro - Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        btnLogin.addActionListener(this::handleLogin);
        btnRegister.addActionListener(this::handleRegister);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        // Gửi yêu cầu đăng nhập
        socketHandle.sendMessage("LOGIN|" + username + "|" + password);

        try {
            String response = socketHandle.receiveMessage();
            if (response == null) {
                JOptionPane.showMessageDialog(this, "Server not responding!");
                return;
            }

            if (response.equalsIgnoreCase("LOGIN_SUCCESS")) {
                JOptionPane.showMessageDialog(this, "✅ Login successful!");
                // TODO: mở giao diện game chính tại đây
                this.dispose();
                new HomePageFrame(socketHandle, username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Login failed. Please check username/password.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error receiving data from server!");
        }
    }

    private void handleRegister(ActionEvent e) {
        dispose();
        new RegisterFrame(socketHandle).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
