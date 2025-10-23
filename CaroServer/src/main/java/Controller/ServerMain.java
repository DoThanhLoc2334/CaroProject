package Controller;

import Model.UserManager;
import Model.RoomManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 5000; 

    public static void main(String[] args) {
        try {
            UserManager userManager = new UserManager();
            RoomManager roomManager = new RoomManager();
            System.out.println("Server already running!");
            
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, userManager, roomManager);
                Thread thread = new Thread(clientHandler);
                thread.start();
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}