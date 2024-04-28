import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Server.java Class
 *
 * Purdue University CS18000 Spring 2024
 *
 * @author Giancarlo Guccione
 * @author Steven Krauter
 * @author Justin Lin
 * @author Wael Harith
 * @author Chase Gamble
 * @version 1.0 April 2024
 */

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

            // Check if the user already exists
            if (this.databaseHelper.readUser(username) != null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Username is taken");
                this.writer.flush();
                return;
            }

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
            // REMINDER: Remove console output
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
            // REMINDER: Remove console output
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
            // REMINDER: Remove console output
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
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
            return;
        }

    }

    public void getMessageHistory() {
        try {
            String clientUsername = this.reader.readLine();
            String otherUsername = this.reader.readLine();

            // Check if the user exists
            User clientUser = this.databaseHelper.readUser(clientUsername);
            User otherUser = this.databaseHelper.readUser(otherUsername);
            if (clientUser == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid client user");
                this.writer.flush();
                return;
            } else if (otherUser == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid other user");
                this.writer.flush();
                return;
            }

            // Get message history
            MessageHistory history = this.databaseHelper.readMessageHistory(clientUsername, otherUsername);
            if (history == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("No message history found!");
                this.writer.flush();
                return;
            }

            // Send back message history contents
            this.writer.println("SUCCESS");
            this.writer.println(history.getUser1());
            this.writer.println(history.getUser2());
            this.writer.println(history.getMessages().length);
            for (String message : history.getMessages()) {
                this.writer.println(message);
            }
            this.writer.flush();
        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
        }
    }

    public void sendMessage() {
        try {
            String senderUsername = this.reader.readLine();
            String receiverUsername = this.reader.readLine();

            // Check if the user exists
            User sender = this.databaseHelper.readUser(senderUsername);
            User receiver = this.databaseHelper.readUser(receiverUsername);
            if (sender == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid sender");
                this.writer.flush();
                return;
            } else if (receiver == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid receiver");
                this.writer.flush();
                return;
            }

            String messageText = this.reader.readLine();

            // Check if blocked
            for (String blockedUsername : receiver.getBlockedUsers()) {
                if (blockedUsername.equals(senderUsername)) {
                    // Send back an error
                    this.writer.println("ERROR");
                    this.writer.println("Sender is blocked");
                    this.writer.flush();
                    return;
                }
            }

            // Check if friend when friendOnly is on
            if (receiver.isFriendsOnly()) {
                boolean isFriend = false;
                for (String friendsUsername : receiver.getFriends()) {
                    if (friendsUsername.equals(senderUsername)) {
                        isFriend = true;
                        break;
                    }
                }

                if (!isFriend) {
                    // Send back an error
                    this.writer.println("ERROR");
                    this.writer.println("Sender is not a friend");
                    this.writer.flush();
                    return;
                }
            }

            Message message = new Message(senderUsername, receiverUsername, messageText);
            if (message == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to create message");
                this.writer.flush();
                return;
            }

            MessageHistory messageHistory = databaseHelper.readMessageHistory(senderUsername, receiverUsername);
            if (messageHistory == null) {
                messageHistory = new MessageHistory(senderUsername, receiverUsername, new ArrayList<Message>());
            }

            if (!messageHistory.addMessage(senderUsername, messageText)) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to add message");
                this.writer.flush();
                return;
            }

            if (!databaseHelper.writeMessageHistory(messageHistory)) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to write new message history");
                this.writer.flush();
                return;
            }

            // When sent successfully
            writer.println("SUCCESS");
            writer.flush();
        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            writer.println("ERROR");
            writer.println(e.getMessage());
            writer.flush();
        }
    }

    public void removeMessage() {
        try {
            String senderUsername = this.reader.readLine();
            String receiverUsername = this.reader.readLine();

            // Check if the user exists
            User sender = this.databaseHelper.readUser(senderUsername);
            User receiver = this.databaseHelper.readUser(receiverUsername);
            if (sender == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid sender");
                this.writer.flush();
                return;
            } else if (receiver == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Invalid receiver");
                this.writer.flush();
                return;
            }

            int messageIndex = Integer.parseInt(this.reader.readLine());

            MessageHistory messageHistory = databaseHelper.readMessageHistory(senderUsername, receiverUsername);
            if (messageHistory == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("No message history");
                this.writer.flush();
                return;
            }

            if (!messageHistory.removeMessage(senderUsername, messageIndex)) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to remove message");
                this.writer.flush();
                return;
            }

            if (!databaseHelper.writeMessageHistory(messageHistory)) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Failed to write new message history");
                this.writer.flush();
                return;
            }

            // When sent successfully
            writer.println("SUCCESS");
            writer.flush();
        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            writer.println("ERROR");
            writer.println(e.getMessage());
            writer.flush();
        }
    }

    public void findUser() {
        try {
            String username = reader.readLine();

            User foundUser = this.databaseHelper.readUser(username);

            if (foundUser == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("User not found");
                this.writer.flush();
                return;
            }

            File userPFP = new File("images/" + foundUser.getUserPFPFile());

            // Check profile picture
            if (userPFP == null || !userPFP.exists() || !userPFP.canRead()) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("User profile picture could not be read");
                this.writer.flush();
                return;
            }

            // Send back user data
            writer.println("SUCCESS");
            writer.println(foundUser.getUsername());
            writer.println(foundUser.getAge());
            writer.println(foundUser.isFriendsOnly());
            writer.println(foundUser.getFriends().size());
            for (String friend : foundUser.getFriends()) {
                writer.println(friend);
            }
            writer.println(foundUser.getBlockedUsers().size());
            for (String block : foundUser.getBlockedUsers()) {
                writer.println(block);
            }

            // Read profile picture file to a byte array
            int fileLength = (int) userPFP.length();
            byte[] buffer = new byte[fileLength];
            BufferedInputStream bufferIn = new BufferedInputStream(new FileInputStream(userPFP));
            bufferIn.read(buffer, 0, fileLength);

            // Base64 encode the file data
            String encoded = Base64.getEncoder().encodeToString(buffer);
            writer.println(encoded.length());

            // Send encoded file data in chunks because it is too large to send in one flush
            int offset = 0;
            while (offset < encoded.length()) {
                int sectionEnd = offset + 1000;
                if (sectionEnd > encoded.length()) {
                    sectionEnd = encoded.length();
                }
                String section = encoded.substring(offset, sectionEnd);
                writer.println(section);
                writer.flush();
                offset += 1000;
            }
        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
        }
    }

    public void searchUser() {
        try {
            String usernameToken = reader.readLine();

            ArrayList<String> foundUsernames = databaseHelper.searchUser(usernameToken);

            if (foundUsernames == null || foundUsernames.size() == 0) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("No users found");
                this.writer.flush();
                return;
            }

            // Send back user data
            int maxResults = 10; // Cap results to a certain amount
            writer.println("SUCCESS");
            if (foundUsernames.size() > maxResults) {
                writer.println(maxResults);
                for (int i = 0; i < maxResults; i++) {
                    writer.println(foundUsernames.get(i));
                }
            } else {
                writer.println(foundUsernames.size());
                for (String username : foundUsernames) {
                    writer.println(username);
                }
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
        }
    }

    public void changeVisibility() {
        try {
            String username = this.reader.readLine();
            boolean condition = Boolean.parseBoolean(this.reader.readLine());

            User user = this.databaseHelper.readUser(username);

            if (user == null) {
                // Send back an error
                this.writer.println("ERROR");
                this.writer.println("Error with user changing condition");
                this.writer.flush();
                return;
            }

            if (user.isFriendsOnly() != condition) {
                //Sets condition
                user.setFriendsOnly(condition);
                this.databaseHelper.writeUser(user);
            }

            //Writes back if completion
            writer.println("SUCCESS");
            writer.flush();

        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();

            // Send back an error
            this.writer.println("ERROR");
            this.writer.println(e.getMessage());
            this.writer.flush();
            return;
        }
    }

    // Server handler that is run by the thread
    public void run() {
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
                        case "FIND_USER":
                            this.findUser();
                            break;
                        case "SEARCH_USER":
                            this.searchUser();
                            break;
                        case "ADD_FRIEND":
                            this.addFriend();
                            break;
                        case "BLOCK":
                            this.blockUser();
                            break;
                        case "MESSAGE_HISTORY":
                            this.getMessageHistory();
                            break;
                        case "SEND_MESSAGE":
                            this.sendMessage();
                            break;
                        case "REMOVE_MESSAGE":
                            this.removeMessage();
                            break;
                        case "FRIENDS_ONLY":
                            this.changeVisibility();
                            break;
                        default:
                            // REMINDER: Remove console output
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
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();

                // Create a new thread with the current socket
                Thread serverThread = new Thread(new Server(socket));
                serverThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}