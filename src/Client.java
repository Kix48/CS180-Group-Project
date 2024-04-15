import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

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

            // TODO: Remove console output
            System.out.println("Connected to server");

            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new PrintWriter(this.socket.getOutputStream());

        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();
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
            // TODO: Remove console output
            e.printStackTrace();
        }
    }

    public boolean register(String username, String password, int age, File userPFP) {
        //
        // String sanitization
        // TODO: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            return false;
        }

        // Check password (Not empty, size check, no newline or tab)
        if (password == null || password.equals("") || (password.length() > 32)
                || password.contains("\n") || password.contains("\t")) {
            return false;
        }

        // Remove extra whitespaces
        username = username.trim();
        password = password.trim();

        // Check age (Greater than 0)
        if (age <= 0) {
            return false;
        }

        // Check profile picture
        if (userPFP == null || !userPFP.exists() || !userPFP.canRead()) {
            return false;
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
                System.out.printf("%s: %s\n", requestResult, resultMessage);
            } else {
                System.out.println("Registration succeeded!");
                return true;
            }
        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public boolean login(String username, String password) {
        //
        // String sanitization
        // TODO: Add specific error messages (Phase 3)
        //

        // Check username (Not empty, size check, no newline or tab)
        if (username == null || username.equals("") || (username.length() > 16)
                || username.contains("\n") || username.contains("\t")) {
            return false;
        }

        // Check password (Not empty, size check, no newline or tab)
        if (password == null || password.equals("") || (password.length() > 32)
                || password.contains("\n") || password.contains("\t")) {
            return false;
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
                System.out.printf("%s: %s\n", requestResult, resultMessage);
            } else {
                clientUsername = username;    // Assigns current user their name
                System.out.println("Login succeeded! Welcome " + clientUsername);
                return true;
            }
        } catch (Exception e) {
            // TODO: Remove console output
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public boolean addFriend(String friendUsername) {

        //
        // String sanitization
        // TODO: Add specific error messages (Phase 3)
        //

        // Check friendUsername & username (Not empty, size check, no newline or tab)
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
        // TODO: Add specific error messages (Phase 3)
        //

        // Check friendUsername & username (Not empty, size check, no newline or tab)
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

    public boolean sendMessage(String receiver, String message) {
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
            // TODO: Remove console output
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeMessage(int messageIdx) {
        return false;
    }

    public boolean setFriendsOnly(boolean friendsOnly) {

        try {
            writer.println();
            writer.println("FRIENDSONLY");
            writer.println(clientUsername);
            writer.println(friendsOnly);
            writer.flush();

            //Read Results
            String resultOutput = reader.readLine();

            if (!resultOutput.equals("SUCCESS")) {
                String resultMessage = reader.readLine();
                System.out.printf("%s: %s\n", resultOutput, resultMessage);
            } else {
                System.out.println("Option successfully changed!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
