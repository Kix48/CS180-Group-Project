import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper implements DatabaseHelperInterface {
    private final String USERS_DIRECTORY = "users/";
    private final String MESSAGES_DIRECTORY = "messages/";
    private final String PHOTOS_DIRECTORY = "photos/";

    /*
    User file format:

    {Username} -> String
    {Password} -> String
    {Age} -> int
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

            return new User(readUsername, readPassword, readAge, readFriends, readBlockedUsers, friendsOnly);

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



}
