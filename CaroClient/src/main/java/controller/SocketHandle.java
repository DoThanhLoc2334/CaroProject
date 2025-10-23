package Controller;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Class quản lý kết nối với server
public class SocketHandle {
    
    // Singleton pattern - chỉ có 1 instance
    private static volatile SocketHandle instance;

    // Socket kết nối với server
    private Socket socket;
    
    // Để gửi tin nhắn đến server
    private PrintWriter out;
    
    // Để đọc tin nhắn từ server
    private BufferedReader in;
    
    // Kiểm tra có đang lắng nghe không
    private volatile boolean listening = false;

    // Tạo kết nối với server
    public SocketHandle(String host, int port) throws IOException {
        System.out.println("Dang ket noi den server " + host + ":" + port + "...");
        
        // Tạo Socket kết nối với server
        socket = new Socket(host, port);
        
        // Tạo PrintWriter để gửi tin nhắn
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        
        // Tạo BufferedReader để đọc tin nhắn
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        
        System.out.println("Da ket noi thanh cong den server!");
    }

    // Lấy instance duy nhất
    public static SocketHandle getInstance() {
        if (instance == null) {
            synchronized (SocketHandle.class) {
                if (instance == null) {
                    try {
                        System.out.println("Tao SocketHandle instance moi...");
                        instance = new SocketHandle("172.20.10.9", 5000);
                    } catch (IOException e) {
                        System.err.println("Khong the ket noi den server: " + e.getMessage());
                        throw new RuntimeException("Cannot connect to server", e);
                    }
                }
            }
        }
        return instance;
    }

    // Set instance (dùng cho testing)
    public static void setInstance(SocketHandle sh) {
        instance = sh;
    }

    // Gửi tin nhắn đến server
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
            System.out.println("Gui tin nhan den server: " + msg);
        } else {
            System.err.println("Khong the gui tin nhan - chua ket noi server");
        }
    }

    // Đọc tin nhắn từ server (blocking)
    public String receiveMessage() throws IOException {
        String msg = in.readLine();
        System.out.println("Nhan tin nhan tu server: " + msg);
        return msg;
    }

    // Bắt đầu lắng nghe tin nhắn từ server
    public void startListening() {
        if (listening) {
            System.out.println("Da dang lang nghe roi!");
            return;
        }
        
        listening = true;
        System.out.println("Bat dau lang nghe tin nhan tu server...");

        // Tạo Thread mới để lắng nghe tin nhắn
        Thread listenerThread = new Thread(() -> {
            System.out.println("Thread lang nghe bat dau chay");
            
            try {
                String line;
                // Vòng lặp đọc tin nhắn từ server
                while ((line = in.readLine()) != null) {
                    System.out.println("Nhan tin nhan tu server: " + line);
                    // Gửi tin nhắn đến GameController để xử lý
                    GameController.getInstance().onRawMessage(line);
                }
            } catch (IOException e) {
                System.err.println("Loi khi lang nghe: " + e.getMessage());
            } finally {
                listening = false;
                System.out.println("Dung lang nghe tin nhan");
            }
        }, "socket-listener");
        
        // Đặt thread là daemon để tự động kết thúc khi app đóng
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        System.out.println("Thread lang nghe da duoc khoi dong!");
    }

    // Đóng kết nối với server
    public void close() {
        System.out.println("Dang dong ket noi voi server...");
        
        try {
            // Flush dữ liệu còn lại
            if (out != null) out.flush();
            
            // Đóng socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Da dong socket");
            }
            
            // Đóng BufferedReader
            if (in != null) {
                in.close();
                System.out.println("Da dong BufferedReader");
            }
            
            // Đóng PrintWriter
            if (out != null) {
                out.close();
                System.out.println("Da dong PrintWriter");
            }
            
            System.out.println("Da dong ket noi thanh cong!");
            
        } catch (IOException e) {
            System.err.println("Loi khi dong ket noi: " + e.getMessage());
        }
    }
}
