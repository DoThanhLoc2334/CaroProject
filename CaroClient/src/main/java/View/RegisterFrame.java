package View;

import Controller.SocketHandle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class RegisterFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnBack;
    private SocketHandle socketHandle;

    public RegisterFrame(SocketHandle socketHandle) {
        this.socketHandle = socketHandle;
        initComponents();
    }

    private void initComponents() {
        setTitle("Register");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblTitle = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        btnRegister = new JButton("Register");
        btnBack = new JButton("Back");

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(lblTitle);
        panel.add(lblUsername);
        panel.add(txtUsername);
        panel.add(lblPassword);
        panel.add(txtPassword);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnRegister);
        btnPanel.add(btnBack);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnRegister.addActionListener(this::onRegister);
        btnBack.addActionListener(this::onBack);
    }

    private void onRegister(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        try {
            socketHandle.sendMessage("REGISTER|" + username + "|" + password);
            String response = socketHandle.receiveMessage();

            if (response == null) {
                JOptionPane.showMessageDialog(this, "No response from server!");
                return;
            }

            switch (response) {
                case "REGISTER_SUCCESS":
                    JOptionPane.showMessageDialog(this, "✅ Registration successful! You can login now.");
                    dispose();
                    new LoginFrame(socketHandle).setVisible(true);
                    break;
                case "REGISTER_FAILED":
                    JOptionPane.showMessageDialog(this, "❌ Username already exists or registration failed!");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown response from server: " + response);
                    break;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error communicating with server!");
        }
    }

    private void onBack(ActionEvent e) {
        dispose();
        new LoginFrame(socketHandle).setVisible(true);
    }

    // Test độc lập (chạy riêng frame này)
    public static void main(String[] args) {
        try {
            SocketHandle socketHandle = new SocketHandle("localhost", 5000);
            SwingUtilities.invokeLater(() -> new RegisterFrame(socketHandle).setVisible(true));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server!");
        }
    }
}

