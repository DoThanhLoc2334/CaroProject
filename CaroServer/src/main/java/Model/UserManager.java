package Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import Util.HashUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String FILE_PATH = "users.json";
    private final Gson gson = new Gson();
    private final List<User> users = new ArrayList<>();

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                saveUsers(); 
            }
            FileReader reader = new FileReader(FILE_PATH);
            Type listType = new TypeToken<ArrayList<User>>() {}.getType();
            List<User> loaded = gson.fromJson(reader, listType);
            reader.close();
            if (loaded != null) {
                users.addAll(loaded);
            }
            System.out.println("[UserManager] Loaded " + users.size() + " users.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean register(String username, String password) {
        if (getUserByName(username) != null) {
            return false; 
        }
        String hash = HashUtil.hashPassword(password);
        users.add(new User(username, hash));
        saveUsers();
        System.out.println("[UserManager] New user registered: " + username);
        return true;
    }

    public synchronized User login(String username, String password) {
        User user = getUserByName(username);
        if (user != null && user.getPasswordHash().equals(HashUtil.hashPassword(password))) {
            user.setOnline(true);
            System.out.println("[UserManager] " + username + " logged in.");
            return user;
        }
        return null;
    }

    public synchronized void logout(String username) {
        User user = getUserByName(username);
        if (user != null) {
            user.setOnline(false);
            System.out.println("[UserManager] " + username + " logged out.");
        }
    }

    private User getUserByName(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
