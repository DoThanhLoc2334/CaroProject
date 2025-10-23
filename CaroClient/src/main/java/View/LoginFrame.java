package View;

import Controller.GameController;
import Controller.SocketHandle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * LoginFrame - Login screen for the Caro game
 * This is a simple class to create the login interface
 */
public class LoginFrame extends JFrame {

    // UI components
    private JTextField txtUsername;        // Username input field
    private JPasswordField txtPassword;     // Password input field
    private JButton btnLogin;              // Login button
    private JButton btnRegister;           // Register button
    private SocketHandle socketHandle;      // Connection to server

    /**
     * Constructor 1: Create a new LoginFrame and connect to server
     */
    public LoginFrame() {
        try {
            this.socketHandle = new SocketHandle("localhost", 5000);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, " Cannot connect to server!");
            return;
        }
        initComponents();
    }

    /**
     * Constructor 2: Create LoginFrame with an existing connection
     */
    public LoginFrame(SocketHandle socketHandle) {
        this.socketHandle = socketHandle;
        initComponents();
    }

    private void initComponents() {
        // Window settings
        setTitle("Caro - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));

        // UI components (no icons)
        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");

        // Add components to form
        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        formPanel.add(lblPass);
        formPanel.add(txtPassword);
        formPanel.add(btnLogin);
        formPanel.add(btnRegister);

        // Add form to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Button actions
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
    }

    /**
     * Handle login action
     */
    private void handleLogin() {
        // Get input
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required information!");
            return;
        }

        // Send login to server
        socketHandle.sendMessage("LOGIN|" + username + "|" + password);

        // Receive server response
        try {
            String response = socketHandle.receiveMessage();
            
            // Check if server responded
            if (response == null) {
                JOptionPane.showMessageDialog(this, "Server did not respond!");
                return;
            }

            // Evaluate login result
            if ("LOGIN_SUCCESS".equalsIgnoreCase(response)) {
                // Login successful
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Save socket instance and start listening
                SocketHandle.setInstance(socketHandle);
                socketHandle.startListening();

                // Move to main screen
                HomePageFrame home = new HomePageFrame(socketHandle, username);
                GameController.getInstance().attachHome(home);
                this.dispose(); // Close login screen
                home.setVisible(true); // Show main screen

            } else {
                // Login failed
                JOptionPane.showMessageDialog(this, "Login failed. Please check your username and password.");
            }
        } catch (IOException ex) {
            // Handle server communication error
            JOptionPane.showMessageDialog(this, "Error receiving data from server!");
        }
    }

    /**
     * Handle register action
     */
    private void handleRegister() {
        // Close login screen
        dispose();
        // Open register screen
        new RegisterFrame(socketHandle).setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}