package View;

import Controller.GameController;
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

    public LoginFrame() {
        try {
            this.socketHandle = new SocketHandle("172.20.10.9", 5000);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Cannot connect to server!");
            return;
        }
        initComponents();
    }

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

        socketHandle.sendMessage("LOGIN|" + username + "|" + password);

        try {
            String response = socketHandle.receiveMessage();
            if (response == null) {
                JOptionPane.showMessageDialog(this, "Server not responding!");
                return;
            }

            if ("LOGIN_SUCCESS".equalsIgnoreCase(response)) {
                JOptionPane.showMessageDialog(this, "✅ Login successful!");

                SocketHandle.setInstance(socketHandle);
                socketHandle.startListening(); // từ đây về sau KHÔNG dùng receiveMessage ở UI nữa

                HomePageFrame home = new HomePageFrame(socketHandle, username);
                GameController.getInstance().attachHome(home);
                this.dispose();
                home.setVisible(true);

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
