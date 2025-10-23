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
            System.out.println("Server da san sang!");
            
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server dang chay tren port: " + PORT);
            System.out.println("Cho client ket noi...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Co client moi ket noi: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, userManager, roomManager);
                Thread thread = new Thread(clientHandler);
                thread.start();
                
                System.out.println("Da tao Thread cho client");
            }
            
        } catch (IOException e) {
            System.out.println("Loi khi chay server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}