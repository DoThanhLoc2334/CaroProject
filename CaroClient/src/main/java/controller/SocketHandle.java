package Controller;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Class quản lý kết nối với server
public class SocketHandle {
    private static volatile SocketHandle instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening = false;

    public SocketHandle(String host, int port) throws IOException {
        System.out.println("Connecting to server " + host + ":" + port + "...");
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        
        System.out.println("Successfully connected to the server!");
    }

    // Lấy instance duy nhất
    public static SocketHandle getInstance() {
        if (instance == null) {
            synchronized (SocketHandle.class) {
                if (instance == null) {
                    try {
                        System.out.println("Creating a new SocketHandle instance...");
                        instance = new SocketHandle("172.20.10.9", 5000);
                    } catch (IOException e) {
                        System.err.println("Cannot connect to server: " + e.getMessage());
                        throw new RuntimeException("Cannot connect to server", e);
                    }
                }
            }
        }
        return instance;
    }

    public static void setInstance(SocketHandle sh) {
        instance = sh;
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
            System.out.println("Sent message to server: " + msg);
        } else {
            System.err.println("Cannot send message - not connected to server");
        }
    }

    public String receiveMessage() throws IOException {
        String msg = in.readLine();
        System.out.println("Received message from server: " + msg);
        return msg;
    }

    // Bắt đầu lắng nghe tin nhắn từ server
    public void startListening() {
        if (listening) {
            System.out.println("Already listening!");
            return;
        }
        
        listening = true;
        System.out.println("Starting to listen for messages from server...");

        // Tạo Thread mới để lắng nghe tin nhắn
        Thread listenerThread = new Thread(() -> {
            System.out.println("Listener thread started running");
            
            try {
                String line;
                // Vòng lặp đọc tin nhắn từ server
                while ((line = in.readLine()) != null) {
                    System.out.println("Received message from server: " + line);
                    // Gửi tin nhắn đến GameController để xử lý
                    GameController.getInstance().onRawMessage(line);
                }
                    
            } catch (IOException e) {
                System.err.println("Error while listening: " + e.getMessage());
            } finally {
                listening = false;
                System.out.println("Stopped listening for messages");
            }
        }, "socket-listener");
        
        // Đặt thread là daemon để tự động kết thúc khi app đóng
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        System.out.println("Listener thread has started!");
    }

    // Đóng kết nối với server
    public void close() {
        System.out.println("Closing connection to server...");
        
        try {
            if (out != null) out.flush();
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed");
            }
            
            if (in != null) {
                in.close();
                System.out.println("BufferedReader closed");
            }
            
            if (out != null) {
                out.close();
                System.out.println("PrintWriter closed");
            }
            
            System.out.println("Connection closed successfully!");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}