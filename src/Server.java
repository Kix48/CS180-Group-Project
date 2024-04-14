import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;
import java.util.Base64;

public class Server implements ServerInterface, Runnable {
    private static final int PORT = 4444;
    private Socket currentSocket;
    private DatabaseHelper databaseHelper;
    private BufferedReader reader;
    private PrintWriter writer;

    public Server(Socket currentSocket) {
        this.currentSocket = currentSocket;
    }

    public void registerUser() {
        try {
            String username = this.reader.readLine();
            String password = this.reader.readLine();
            int age = Integer.parseInt(this.reader.readLine());
            // Read file data

            // Check if the user already exists
            if (this.databaseHelper.readUser(username) != null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Username is taken");
                this.writer.flush();
                return;
            }

            // Write profile picture to file
            String pictureName = username + " - PFP.png";
            int fileLength = Integer.parseInt(this.reader.readLine());

            // Read in the Base64 encoded chunks of the file
            String encoded = "";
            do {
                encoded += this.reader.readLine();
            } while (encoded.length() < fileLength);

            // Decode the file data
            byte[] decoded = Base64.getDecoder().decode(encoded);

            if (!this.databaseHelper.writeImage(ImageIO.read(new ByteArrayInputStream(decoded)), pictureName)) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to write profile picture");
                this.writer.flush();
                return;
            }

            // Write user to file
            if (!this.databaseHelper.writeUser(new User(username, password, age, pictureName))) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to write user");
                this.writer.flush();
                return;
            }
        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
            return;
        }

        // Send successful response
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

        this.databaseHelper = new DatabaseHelper();

        try {
            // Instantiate reader and writer
            this.reader = new BufferedReader(new InputStreamReader(this.currentSocket.getInputStream()));
            this.writer = new PrintWriter(this.currentSocket.getOutputStream());

            // Keep the thread alive
            while (this.reader.read() != -1) {
                if (this.reader.ready()) {
                    String requestType = this.reader.readLine();

                    switch (requestType) {
                        case "REGISTER":
                            this.registerUser();
                            break;
                        default:
                            // TODO: Remove console output
                            System.out.println("Unknown request received!");

                            // Empty the input stream
                            if (this.reader.ready()) {
                                String line = this.reader.readLine();
                                while (line != null) {
                                    line = this.reader.readLine();
                                }
                            }

                            // Send back an error
                            this.writer.println("ERROR");
                            this.writer.println("Unknown request received");
                            this.writer.flush();
                    }
                }
            }

            // Close resources
            reader.close();
            writer.close();
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
