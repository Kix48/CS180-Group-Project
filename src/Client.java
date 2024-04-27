import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
/**
 * Client.java Class
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

public class Client implements ClientInterface {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 4444;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientUsername; // Current client's name, updates when logged in successfully

    public boolean initialize() {
        try {
            // Cannot use try-with-resources with class field for some reason
            this.socket = new Socket(HOSTNAME, PORT);

            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream());

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void shutdown() {
        try {
            this.socket.close();
            this.reader.close();
            this.writer.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean register(String username, String password, int age, File userPFP) throws Exception {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            throw new Exception("Invalid username (Must be 16 characters or less).");
            //return false;
        }

        // Check password (Not empty, size check, no newline or tab)
        if (password == null || password.equals("") || (password.length() > 32)
                || password.contains("\n") || password.contains("\t")) {
            throw new Exception("Invalid password (Must be 32 characters or less).");
            //return false;
        }

        // Remove extra whitespaces
        username = username.trim();
        password = password.trim();

        // Check age (Greater than 0)
        if (age <= 0) {
            throw new Exception("Age must be greater than 0.");
            //return false;
        }

        // Check profile picture
        if (userPFP == null || !userPFP.exists() || !userPFP.canRead()) {
            throw new Exception("Invalid profile picture.");
            //return false;
        }

        //
        // Data transfer
        //

        try {
            writer.println();
            writer.println("REGISTER");
            writer.println(username);
            writer.println(password);
            writer.println(age);

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

            // Read result
            String requestResult = reader.readLine();
            if (!requestResult.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                //System.out.printf("%s: %s\n", requestResult, resultMessage);
                throw new Exception(String.format("%s: %s.", requestResult, resultMessage));
            } else {
                //System.out.println("Registration succeeded!");
                return true;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("ERROR:")) {
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf(':') + 2));
            } else {
                throw new Exception("Exception while registering user.");
            }
            //return false;
        }

        //return false;
    }

    public boolean login(String username, String password) throws Exception {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            throw new Exception("Invalid username (Must be 16 characters or less).");
            //return false;
        }

        // Check password (Not empty, size check, no newline or tab)
        if (password == null || password.equals("") || (password.length() > 32)
                || password.contains("\n") || password.contains("\t")) {
            throw new Exception("Invalid password (Must be 32 characters or less).");
            //return false;
        }

        // Remove extra whitespaces
        username = username.trim();
        password = password.trim();

        try {
            writer.println();
            writer.println("LOGIN");
            writer.println(username);
            writer.println(password);
            writer.flush();

            // Read result
            String requestResult = reader.readLine();
            if (!requestResult.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                //System.out.printf("%s: %s\n", requestResult, resultMessage);
                throw new Exception(String.format("%s: %s.", requestResult, resultMessage));
            } else {
                clientUsername = username;    // Assigns current user their name
                //System.out.println("Login succeeded, welcome " + clientUsername + "!");
                return true;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("ERROR:")) {
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf(':') + 2));
            } else {
                throw new Exception("Exception while logging in.");
            }
            //return false;
        }

        //return false;
    }
    public User findUser(String username) throws Exception {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            throw new Exception("Invalid username (Must be 16 characters or less).");
            //return null;
        }

        //remove any whitespace
        username = username.trim();

        try {
            //send information
            writer.println();
            writer.println("FIND_USER");
            writer.println(username);
            writer.flush();

            //Read Result
            String resultOutput = reader.readLine();

            if (!resultOutput.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                //System.out.printf("%s: %s\n", requestResult, resultMessage);
                throw new Exception(String.format("%s: %s.", resultOutput, resultMessage));
            } else {
                String readUsername = reader.readLine();
                int readAge = Integer.parseInt(reader.readLine());
                boolean readFriendsOnly = Boolean.parseBoolean(reader.readLine());

                int fileLength = Integer.parseInt(reader.readLine());

                // Read in the Base64 encoded chunks of the file
                String encoded = "";
                do {
                    encoded += reader.readLine();
                } while (encoded.length() < fileLength);

                // Decode the file data
                byte[] decoded = Base64.getDecoder().decode(encoded);

                BufferedImage readPFP = ImageIO.read(new ByteArrayInputStream(decoded));

                return new User(readUsername, readAge, readPFP, readFriendsOnly);
            }

        } catch (Exception e) {
            if (e.getMessage().contains("ERROR:")) {
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf(':') + 2));
            } else {
                throw new Exception("Exception while finding user.");
            }
            //return false;
        }

        //return null;
    }

    public boolean addFriend(String friendUsername) {

        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check friendUsername (Not empty, size check, no newline or tab)
        if (friendUsername == null || friendUsername.equals("") || (friendUsername.length() > 16)
                || friendUsername.contains("\n") || friendUsername.contains("\t")) {
            return false;
        }

        //remove any whitespace
        friendUsername = friendUsername.trim();

        try {
            //send information
            writer.println();
            writer.println("ADD_FRIEND");
            writer.println(clientUsername);
            writer.println(friendUsername);
            writer.flush();

            //Read Result
            String resultOutput = reader.readLine();

            if (!resultOutput.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", resultOutput, resultMessage);
            } else {
                System.out.println("Friend successfully added!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean blockUser(String usernameToBlock) {

        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check usernameToBlock (Not empty, size check, no newline or tab)
        if (usernameToBlock == null || usernameToBlock.equals("") || (usernameToBlock.length() > 16)
                || usernameToBlock.contains("\n") || usernameToBlock.contains("\t")) {
            return false;
        }

        //remove any whitespace
        usernameToBlock = usernameToBlock.trim();

        try {
            //send information
            writer.println();
            writer.println("BLOCK");
            writer.println(clientUsername);
            writer.println(usernameToBlock);
            writer.flush();

            //Read Result
            String resultOutput = reader.readLine();

            if (!resultOutput.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", resultOutput, resultMessage);
            } else {
                System.out.println("User Successfully Blocked!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public MessageHistory getMessageHistory(String username) {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            return null;
        }

        // Remove extra whitespaces
        username = username.trim();

        try {
            writer.println();
            writer.println("MESSAGE_HISTORY");
            writer.println(clientUsername);
            writer.println(username);
            writer.flush();

            // Read result
            String requestResult = reader.readLine();
            if (!requestResult.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", requestResult, resultMessage);
            } else {
                String readUser1 = reader.readLine();
                String readUser2 = reader.readLine();
                int readMessagesLength = Integer.parseInt(reader.readLine());

                ArrayList<Message> readMessages = new ArrayList<Message>();
                for (int i = 0; i < readMessagesLength; i++) {
                    // [DATE] SENDER-RECEIVER: MESSAGE

                    String readMessage = reader.readLine();
                    int rightBracketPos = readMessage.indexOf("]");
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
                    Date readDate = dateFormatter.parse(readMessage.substring(1, rightBracketPos));
                    int dashPos = readMessage.indexOf("-", rightBracketPos);
                    int colonPos = readMessage.indexOf(":", rightBracketPos);
                    String readSender = readMessage.substring(rightBracketPos + 2, dashPos);
                    String readReceiver = readMessage.substring(dashPos + 1, colonPos);
                    String readMessageStr = readMessage.substring(colonPos + 2);

                    readMessages.add(new Message(readSender, readReceiver, readMessageStr, readDate));
                }

                System.out.println("Received message history successfully!");
                return new MessageHistory(readUser1, readUser2, readMessages);
            }
        } catch (Exception e) {
            // REMINDER: Remove console output
            e.printStackTrace();
            return null;
        }

        return null;
    }

    public boolean sendMessage(String receiver, String message) {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check receiver username (Not empty, size check, no newline or tab)
        if (receiver == null || receiver.equals("") || (receiver.length() > 16)
                || receiver.contains("\n") || receiver.contains("\t")) {
            return false;
        }

        // Remove extra whitespaces
        receiver = receiver.trim();

        // Message can only be 256 characters
        if (message.length() > 256) {
            return false;
        }

        try {
            writer.println();
            writer.println("SEND_MESSAGE");

            writer.println(clientUsername);
            writer.println(receiver);
            writer.println(message);
            writer.flush();

            String requestResult = reader.readLine();
            if (!(requestResult.equals("SUCCESS"))) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", requestResult, resultMessage);
                return false;
            } else {
                System.out.println("Message sent successfully!");
                return true;
            }
        } catch (IOException e) {
            // REMINDER: Remove console output
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeMessage(String receiver, int messageIdx) {
        //
        // String sanitization
        // REMINDER: Add specific error messages (Phase 3)
        //

        // Check receiver username (Not empty, size check, no newline or tab)
        if (receiver == null || receiver.equals("") || (receiver.length() > 16)
                || receiver.contains("\n") || receiver.contains("\t")) {
            return false;
        }

        // Index must be positive
        if (messageIdx < 0) {
            return false;
        }

        try {
            writer.println();
            writer.println("REMOVE_MESSAGE");

            writer.println(clientUsername);
            writer.println(receiver);
            writer.println(messageIdx);
            writer.flush();

            String requestResult = reader.readLine();
            if (!(requestResult.equals("SUCCESS"))) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", requestResult, resultMessage);
                return false;
            } else {
                System.out.println("Message removed successfully!");
                return true;
            }
        } catch (IOException e) {
            // REMINDER: Remove console output
            e.printStackTrace();
            return false;
        }
    }

    public boolean setFriendsOnly(boolean friendsOnly) throws Exception {

        try {
            writer.println();
            writer.println("FRIENDS_ONLY");
            writer.println(clientUsername);
            writer.println(friendsOnly);
            writer.flush();

            //Read Results
            String resultOutput = reader.readLine();

            if (!resultOutput.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                //System.out.printf("%s: %s\n", requestResult, resultMessage);
                throw new Exception(String.format("%s: %s.", resultOutput, resultMessage));
            } else {
                //System.out.println("Option successfully changed!");
                return true;
            }

        } catch (Exception e) {
            if (e.getMessage().contains("ERROR:")) {
                throw new Exception(e.getMessage().substring(e.getMessage().indexOf(':') + 2));
            } else {
                throw new Exception("Exception while changing messaging settings.");
            }
            //return false;
        }

        //return false;
    }
}