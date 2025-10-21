package Controller;

import Model.UserManager;
import Model.RoomManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 5000; 
    private ServerSocket serverSocket;
    private final UserManager userManager;
    private final RoomManager roomManager;

    public ServerMain() {
        userManager = new UserManager();
        roomManager = new RoomManager();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[Server] Started on all interfaces, port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client connected: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket, userManager, roomManager)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerMain server = new ServerMain();
        server.startServer();
    }
}
