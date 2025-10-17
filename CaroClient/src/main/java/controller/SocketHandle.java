/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.*;
import java.net.Socket;

public class SocketHandle {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public SocketHandle(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("[Client] Connected to server " + host + ":" + port);
    }

    // Gửi message
    public void sendMessage(String msg) {
        out.println(msg);
        System.out.println("[Client] Sent: " + msg);
    }

    // Nhận message (blocking)
    public String receiveMessage() throws IOException {
        String msg = in.readLine();
        System.out.println("[Client] Received: " + msg);
        return msg;
    }

    // Đóng kết nối
    public void close() throws IOException {
        socket.close();
        in.close();
        out.close();
    }
}

