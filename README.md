# 🎮 Dự Án Game Caro - Học Java Socket và Multithreading

## 📚 Mục Đích Dự Án
Dự án này được thiết kế đặc biệt cho **sinh viên mới học Java Socket và Multithreading**. Code được viết đơn giản, dễ hiểu với:
- **Comment ngắn gọn** bằng tiếng Việt
- **Log message đơn giản** dễ theo dõi
- **Cấu trúc code cơ bản** phù hợp với newbie
- **Không sử dụng pattern phức tạp**

## 🏗️ Cấu Trúc Dự Án

### Server (CaroServer)
```
CaroServer/
├── src/main/java/
│   ├── Controller/
│   │   ├── ServerMain.java          # Khởi động server
│   │   └── ClientHandler.java       # Xử lý từng client
│   ├── Model/
│   │   ├── UserManager.java        # Quản lý user
│   │   ├── RoomManager.java        # Quản lý phòng chơi
│   │   ├── Room.java              # Class phòng
│   │   └── User.java               # Class user
│   └── Util/
│       └── HashUtil.java           # Mã hóa password
```

### Client (CaroClient)
```
CaroClient/
├── src/main/java/
│   ├── Controller/
│   │   ├── SocketHandle.java       # Quản lý kết nối
│   │   ├── ClientHandler.java      # Xử lý kết nối client
│   │   └── GameController.java     # Logic game
│   ├── Model/
│   │   ├── User.java              # Model user
│   │   ├── Point.java             # Tọa độ
│   │   └── XOButton.java          # Button game
│   └── View/
│       ├── LoginFrame.java         # Màn hình đăng nhập
│       ├── RegisterFrame.java     # Màn hình đăng ký
│       ├── HomePageFrame.java     # Trang chủ
│       ├── RoomManagerFrame.java  # Quản lý phòng
│       └── GameFrame.java         # Màn hình game
```

## 🚀 Cách Chạy Dự Án

### 1. Chạy Server
```bash
cd CaroServer
mvn clean compile exec:java -Dexec.mainClass="Controller.ServerMain"
```

### 2. Chạy Client
```bash
cd CaroClient
mvn clean compile exec:java -Dexec.mainClass="View.LoginFrame"
```

## 📖 Kiến Thức Học Được

### 1. Java Socket Programming (Cơ Bản)
- **ServerSocket**: Lắng nghe kết nối từ client
- **Socket**: Kết nối giữa client và server  
- **BufferedReader/PrintWriter**: Đọc/ghi dữ liệu đơn giản

### 2. Multithreading (Đơn Giản)
- **Thread**: Xử lý nhiều client đồng thời
- **Runnable Interface**: Tạo thread mới
- **Synchronized**: Đảm bảo thread safety cơ bản

### 3. Client-Server Architecture (Cơ Bản)
- **Request-Response**: Gửi lệnh và nhận phản hồi
- **Message Protocol**: Định dạng tin nhắn đơn giản
- **Connection Management**: Quản lý kết nối cơ bản

## 🔧 Các Tính Năng Chính

### Server Features
- ✅ **Đăng ký/Đăng nhập**: Quản lý user với password hash
- ✅ **Tạo phòng**: User có thể tạo phòng chơi
- ✅ **Tham gia phòng**: User có thể tham gia phòng khác
- ✅ **Danh sách phòng**: Xem các phòng đang chờ
- ✅ **Game Logic**: Xử lý nước đi và kiểm tra thắng thua
- ✅ **Multithreading**: Mỗi client có thread riêng

### Client Features
- ✅ **GUI Interface**: Giao diện đồ họa đơn giản
- ✅ **Socket Connection**: Kết nối với server
- ✅ **Real-time Communication**: Giao tiếp thời gian thực
- ✅ **Game Board**: Bàn cờ caro 20x20
- ✅ **User Management**: Đăng ký, đăng nhập

## 📝 Protocol Tin Nhắn

### Client → Server
```
REGISTER|username|password     # Đăng ký
LOGIN|username|password       # Đăng nhập
CREATE_ROOM                    # Tạo phòng
JOIN_ROOM|roomId|username      # Tham gia phòng
LIST_ROOMS                     # Danh sách phòng
MOVE|roomId|x|y               # Đánh nước
LOGOUT|username               # Đăng xuất
```

### Server → Client
```
REGISTER_SUCCESS/FAIL          # Kết quả đăng ký
LOGIN_SUCCESS/FAIL            # Kết quả đăng nhập
ROOM_CREATED|roomId           # Phòng đã tạo
ROOM_JOINED|roomId            # Đã tham gia phòng
ROOM_LIST|...                 # Danh sách phòng
GAME_STARTED|roomId|mark|turn|size  # Game bắt đầu
MOVE_APPLIED|roomId|x|y|mark|turn    # Nước đi
GAME_OVER|roomId|winner|winnerName   # Kết thúc game
```

## 🎯 Điểm Học Tập Quan Trọng

### 1. Socket Programming (Đơn Giản)
```java
// Server: Lắng nghe kết nối
ServerSocket serverSocket = new ServerSocket(5000);
Socket clientSocket = serverSocket.accept();

// Client: Kết nối đến server  
Socket socket = new Socket("localhost", 5000);
```

### 2. Multithreading (Cơ Bản)
```java
// Tạo thread mới cho mỗi client
Thread clientThread = new Thread(new ClientHandler(socket));
clientThread.start();
```

### 3. Thread Safety (Đơn Giản)
```java
// Sử dụng synchronized để đảm bảo an toàn
public synchronized void addRoom(Room room) {
    rooms.add(room);
}
```

## 💡 Đặc Điểm Code Newbie

### 1. Comment Đơn Giản
- Sử dụng tiếng Việt dễ hiểu
- Comment ngắn gọn, không dài dòng
- Giải thích từng bước cơ bản

### 2. Log Message Rõ Ràng
- In ra console để theo dõi
- Sử dụng tiếng Việt
- Không sử dụng emoji phức tạp

### 3. Cấu Trúc Code Cơ Bản
- Không sử dụng design pattern phức tạp
- Method ngắn, dễ hiểu
- Biến đơn giản, tên rõ ràng

## 🐛 Debug và Troubleshooting

### Lỗi Thường Gặp
1. **Port đã được sử dụng**: Thay đổi port trong ServerMain.java
2. **Kết nối bị từ chối**: Kiểm tra IP address trong client
3. **Thread không dừng**: Sử dụng daemon thread

### Debug Tips
- Sử dụng `System.out.println()` để theo dõi luồng xử lý
- Kiểm tra console để xem tin nhắn debug
- Sử dụng breakpoint trong IDE

## 📚 Tài Liệu Tham Khảo

- [Java Socket Programming](https://docs.oracle.com/javase/tutorial/networking/sockets/)
- [Java Multithreading](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Java Collections](https://docs.oracle.com/javase/tutorial/collections/)

## 👨‍💻 Tác Giả
Dự án được thiết kế cho sinh viên mới học Java Socket và Multithreading.

## 📄 License
Dự án này được tạo ra cho mục đích học tập.
