/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(String host, int port) {
        try {
            socket = new Socket("172.20.10.9", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            System.out.println("Cannot connect to server!");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("📤 Sent: " + message);
        }
    }

    public String receiveMessage() {
        try {
            if (in != null) {
                String msg = in.readLine();
                System.out.println("📩 Received: " + msg);
                return msg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

