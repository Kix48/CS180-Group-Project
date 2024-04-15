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

    public void modifyUser() {

    }

    public void deleteUser() {

    }

    public void authenticate() {
        try {
            String username = this.reader.readLine();
            String password = this.reader.readLine();

            // Check if the user exists
            User user = this.databaseHelper.readUser(username);
            if (user == null) {
                // Send back an error
                this.writer.println("ERROR");
                // We know it is the username, but we cannot tell the client for security purposes
                this.writer.println("Invalid username/password");
                this.writer.flush();
                return;
            }

            if (!user.getPassword().equals(password)) {
                // Send back an error
                this.writer.println("ERROR");
                // We know it is the password, but we cannot tell the client for security purposes
                this.writer.println("Invalid username/password");
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

    public void addFriend() {
        try {

            String username = this.reader.readLine();
            String friendUsername = this.reader.readLine();

            User userPerson1 = this.databaseHelper.readUser(username);
            User userFriend = this.databaseHelper.readUser(friendUsername);

            //checks for each User (existence/validity)
            if (userPerson1 == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid username for User adding another");
                this.writer.flush();
                return;
            }

            if (userFriend == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid username friend being added");
                this.writer.flush();
                return;
            }

            //add friend if successful (all works within method within User.java)
            if (userPerson1.addFriends(userFriend.getUsername())) {
                this.databaseHelper.writeUser(userPerson1);
            }

            //send completion if so
            writer.println("SUCCESS");
            writer.flush();

        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
            return;
        }

    }

    public void blockUser() {

        try {

            String username = this.reader.readLine();
            String userToBlock = this.reader.readLine();

            User userPerson1 = this.databaseHelper.readUser(username);
            User userBlock = this.databaseHelper.readUser(userToBlock);

            //checks for each User (existence/validity)
            if (userPerson1 == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid username for User blocking another");
                this.writer.flush();
                return;
            }

            if (userBlock == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid username user being blocked");
                this.writer.flush();
                return;
            }

            //block user if successful (all works within method within User.java)
            if (userPerson1.addBlockedUsers(userBlock.getUsername())) {
                this.databaseHelper.writeUser(userPerson1);
            }

            //send completion if so
            writer.println("SUCCESS");
            writer.flush();

        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
            return;
        }

    }

    public void getMessageHistory() {

    }

    public void sendMessage() {

        try {
            String sender = this.reader.readLine();
            String receiver = this.reader.readLine();
            String messageText = this.reader.readLine();

            Message message = new Message(sender, receiver, messageText);

            // TODO: Send message and add to MessageHistory

            // When sent successfully
            // writer.println("SUCCESS");
            // writer.flush();


        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();

            // Send back an error
            writer.println("ERROR");
            writer.println(e.getMessage());
            writer.flush();
        }

    }

    public void findUser() {

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
                        case "LOGIN":
                            this.authenticate();
                            break;
                        case "ADD_FRIEND":
                            this.addFriend();
                            break;
                        case "BLOCK":
                            this.blockUser();
                            break;
                        case "SEND_MESSAGE":
                            this.sendMessage();
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
