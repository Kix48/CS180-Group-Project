import java.net.*;
import java.io.*;

public class Server implements ServerInterface, Runnable {
    private static final int PORT = 1111;
    private Socket currentSocket;

    public Server(Socket currentSocket) {
        this.currentSocket = currentSocket;
    }

    public boolean createUser(User user) {
        return false;
    }

    public boolean modifyUser(User user) {
        return false;
    }

    public boolean deleteUser(String username) {
        return false;
    }

    public boolean authenticate(String username, String password) {
        return false;
    }

    public MessageHistory getMessageHistory(String username1, String username2) {
        return null;
    }

    public boolean sendMessage(Message message, String username) {
        return false;
    }

    public User findUser(String username) {
        return null;
    }

    // Server handler that is run by the thread
    public void run() {
        // TODO: Remove console output
        System.out.println("Client connected");

        // Keep the thread alive
        // TODO: Keep alive only when connection is still active -> reader.read() != -1
        while (true) {
            // Inner logic
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            // TODO: Remove console output
            System.out.println("Server started");

            while (true) {
                Socket socket = serverSocket.accept();

                // Create a new thread with the current socket
                Thread serverThread = new Thread(new Server(socket));
                serverThread.start();
            }
        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();
        }
    }
}
