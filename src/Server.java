import java.net.*;
import java.io.*;

public class Server implements ServerInterface, Runnable {
    private static final int PORT = 4444;
    private Socket currentSocket;

    public Server(Socket currentSocket) {
        this.currentSocket = currentSocket;
    }

    public void registerUser(BufferedReader reader, PrintWriter writer, DatabaseHelper databaseHelper) {
        // Send response
        writer.println("SUCCESS");
        writer.flush();
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

        DatabaseHelper databaseHelper = new DatabaseHelper();

        try {
            // Instantiate reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.currentSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(this.currentSocket.getOutputStream());

            // Keep the thread alive
            while (reader.read() != -1) {
                if (reader.ready()) {
                    String requestType = reader.readLine();

                    switch (requestType) {
                        case "REGISTER":
                            registerUser(reader, writer, databaseHelper);
                            break;
                        default:
                            // TODO: Remove console output
                            System.out.println("Unknown request received!");

                            // Empty the input stream
                            if (reader.ready()) {
                                String line = reader.readLine();
                                while (line != null) {
                                    line = reader.readLine();
                                }
                            }

                            // Send back an error
                            writer.println("ERROR");
                            writer.println("Unknown request received");
                            writer.flush();
                    }
                }
            }
        } catch (Exception e) {
            // TODO: Make error handling more in-depth
            e.printStackTrace();
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
