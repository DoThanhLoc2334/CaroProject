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

    public SocketHandle(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        System.out.println("[Client] Connected to server " + host + ":" + port);
    }

    public static SocketHandle getInstance() {
        if (instance == null) {
            synchronized (SocketHandle.class) {
                if (instance == null) {
                    try {
                        instance = new SocketHandle("172.20.10.9", 5000); 
                    } catch (IOException e) {
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
            System.out.println("[Client] Sent: " + msg);
        }
    }

    public String receiveMessage() throws IOException {
        String msg = in.readLine();
        System.out.println("[Client] Received(blocking): " + msg);
        return msg;
    }

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
