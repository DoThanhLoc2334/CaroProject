package Controller;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketHandle {
    private static volatile SocketHandle instance;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening = false;

    // Cách 1: cho phép new từ Login/Register
    public SocketHandle(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        System.out.println("[Client] Connected to server " + host + ":" + port);
    }

    // Vẫn giữ singleton cho các nơi đang gọi getInstance()
    public static SocketHandle getInstance() {
        if (instance == null) {
            synchronized (SocketHandle.class) {
                if (instance == null) {
                    try {
                        instance = new SocketHandle("172.20.10.9", 5000); // chỉnh host/port theo server
                    } catch (IOException e) {
                        throw new RuntimeException("Cannot connect to server", e);
                    }
                }
            }
        }
        return instance;
    }

    // Đăng ký instance vừa new vào singleton (rất quan trọng)
    public static void setInstance(SocketHandle sh) {
        instance = sh;
    }

    // Gửi message
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
            System.out.println("[Client] Sent: " + msg);
        }
    }

    // (Tuỳ chọn) Nhận 1 dòng blocking — hạn chế dùng khi đã có listener
    public String receiveMessage() throws IOException {
        String msg = in.readLine();
        System.out.println("[Client] Received(blocking): " + msg);
        return msg;
    }

    // Listener thread, chỉ chạy 1 lần
    public void startListening() {
        if (listening) return;
        listening = true;

        Thread t = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[Client] Received: " + line);
                    GameController.getInstance().onRawMessage(line);
                }
            } catch (IOException e) {
                System.err.println("[Client] Listener stopped: " + e.getMessage());
            } finally {
                listening = false;
            }
        }, "socket-listener");
        t.setDaemon(true);
        t.start();
    }

    public void close() {
        try {
            if (out != null) out.flush();
            if (socket != null && !socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException ignored) {}
    }
}
