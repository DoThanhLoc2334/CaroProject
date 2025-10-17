/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.UserManager;
import Model.User;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserManager userManager;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, UserManager userManager) {
        this.socket = socket;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

//            out.println("Welcome to Caro Server!");
            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
            }
        } catch (IOException e) {
            System.out.println("[ClientHandler] Connection closed: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleMessage(String msg) {
        System.out.println("[Server] Received: " + msg);

        if (msg.startsWith("REGISTER|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];

                boolean success = userManager.register(username, password);

                if (success) {
                    out.println("REGISTER_SUCCESS");
                    System.out.println("[Server] User registered: " + username);
                } else {
                    out.println("REGISTER_FAIL");
                    System.out.println("[Server] Register failed for user: " + username);
                }
            } else {
                out.println("REGISTER_FAIL");
            }

        } else if (msg.startsWith("LOGIN|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];

                User user = userManager.login(username, password);
                if (user != null) {
                    out.println("LOGIN_SUCCESS");
                    System.out.println("[Server] Login success: " + username);
                } else {
                    out.println("LOGIN_FAIL");
                    System.out.println("[Server] Login failed: " + username);
                }
            } else {
                out.println("LOGIN_FAIL");
            }

        } else {
            out.println("UNKNOWN_COMMAND");
            System.out.println("[Server] Unknown command: " + msg);
        }
    }

}
