import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DatabaseHelper implements DatabaseHelperInterface {
    private final String USERS_DIRECTORY = "users/";
    private final String MESSAGES_DIRECTORY = "messages/";
    private final String IMAGES_DIRECTORY = "images/";

    /*
    User file format:

    {Username} -> String
    {Password} -> String
    {Age} -> int
    {UserPFPFile} -> String
    {Friends} -> ArrayList
    {BlockedUsers} -> ArrayList
    {FriendsOnly} -> boolean
     */
    public User readUser(String username) {
        try {
            File file = new File(USERS_DIRECTORY + username + ".txt");

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdir()) {
                    throw new IOException("Failed to create users directory!");
                }
            }

            if (!file.exists()) {
                throw new IOException("User file does not exist!");
            }

            BufferedReader bfr = new BufferedReader(new FileReader(file));

            String readUsername = bfr.readLine();

            String readPassword = bfr.readLine();

            int readAge = Integer.parseInt(bfr.readLine());

            String readUserPFPFile = bfr.readLine();

            String readFriendsData = bfr.readLine();
            readFriendsData = readFriendsData.replace("[", "");
            readFriendsData = readFriendsData.replace("]", "");
            String[] readFriendsArray = readFriendsData.split(", ");
            ArrayList<String> readFriends = new ArrayList<String>(Arrays.asList(readFriendsArray));

            String readBlockedUsersData = bfr.readLine();
            readBlockedUsersData = readBlockedUsersData.replace("[", "");
            readBlockedUsersData = readBlockedUsersData.replace("]", "");
            String[] readBlockedUsersArray = readBlockedUsersData.split(",");
            ArrayList<String> readBlockedUsers = new ArrayList<String>(Arrays.asList(readBlockedUsersArray));
            boolean friendsOnly = Boolean.parseBoolean(bfr.readLine());

            bfr.close();

            return new User(readUsername, readPassword, readAge, readUserPFPFile,
                    readFriends, readBlockedUsers, friendsOnly);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean writeUser(User user) {
        try {
            File file = new File(USERS_DIRECTORY + user.getUsername() + ".txt");

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdir()) {
                    throw new IOException("Failed to create users directory!");
                }
            }

            PrintWriter pw = new PrintWriter(new FileWriter(file, false));
            pw.println(user.getUsername());
            pw.println(user.getPassword());
            pw.println(user.getAge());
            pw.println(user.getUserPFPFile());
            pw.println(user.getFriends());
            pw.println(user.getBlockedUsers());
            pw.println(user.isFriendsOnly());

            pw.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public BufferedImage readImage(String filename) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(IMAGES_DIRECTORY + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public boolean writeImage(BufferedImage image, String filename) {
        try {
            return ImageIO.write(image, "png", new File(IMAGES_DIRECTORY + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public MessageHistory readMessageHistory(String username1, String username2) {
        // Figure out the file name based off the usernames
        // The filename will always have the username which is sorted first alphabetically
        String filename = "";
        if (username1.compareTo(username2) > 0) {
            filename = username2 + "-" + username1;
        } else {
            filename = username1 + "-" + username2;
        }

        try {
            File file = new File(MESSAGES_DIRECTORY + filename + ".txt");

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdir()) {
                    throw new IOException("Failed to create messages directory!");
                }
            }

            if (!file.exists()) {
                throw new IOException("Message file does not exist!");
            }

            BufferedReader bfr = new BufferedReader(new FileReader(file));

            String readUser1 = bfr.readLine();
            String readUser2 = bfr.readLine();

            ArrayList<Message> messages = new ArrayList<Message>();
            String readMessage = bfr.readLine();
            while (readMessage != null) {
                // [DATE] SENDER-RECEIVER: MESSAGE
                int rightBracketPos = readMessage.indexOf("]");
                Date readDate = new Date(Date.parse(readMessage.substring(1, rightBracketPos)));
                int dashPos = readMessage.indexOf("-", rightBracketPos);
                int colonPos = readMessage.indexOf(":", rightBracketPos);
                String readSender = readMessage.substring(rightBracketPos + 2, dashPos);
                String readReceiver = readMessage.substring(dashPos + 1, colonPos);
                String readMessageStr = readMessage.substring(colonPos + 2);

                messages.add(new Message(readSender, readReceiver, readMessageStr, readDate));

                readMessage = bfr.readLine();
            }

            return new MessageHistory(username1, username2, messages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean writeMessageHistory(MessageHistory history) {
        // Figure out the file name based off the usernames
        // The filename will always have the username which is sorted first alphabetically
        String filename = "";
        if (history.getUser1().compareTo(history.getUser2()) > 0) {
            filename = history.getUser2() + "-" + history.getUser1();
        } else {
            filename = history.getUser1() + "-" + history.getUser2();
        }

        try {
            File file = new File(MESSAGES_DIRECTORY + filename + ".txt");

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdir()) {
                    throw new IOException("Failed to create messages directory!");
                }
            }

            PrintWriter pw = new PrintWriter(new FileWriter(file, false));
            pw.println(history.getUser1());
            pw.println(history.getUser2());

            for (String message : history.getMessages()) {
                pw.println(message);
            }

            pw.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
