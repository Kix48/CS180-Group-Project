import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;

public class Client implements ClientInterface {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 1111;

    public boolean register(String username, String password, int age, BufferedImage userPFP) {
        return false;
    }

    public boolean login(String username, String password) {
        return false;
    }

    public boolean addFriend(String friendUsername) {
        return false;
    }

    public boolean blockUser(String username) {
        return false;
    }

    public boolean sendMessage(String receiver, String message) {
        return false;
    }

    public boolean removeMessage(int messageIdx) {
        return false;
    }

    public boolean setFriendsOnly(boolean friendsOnly) {
        return false;
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOSTNAME, PORT)) {
            // TODO: Remove console output
            System.out.println("Connected to server");
        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();
        }
    }
}
