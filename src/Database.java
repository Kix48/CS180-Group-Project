import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Database.java Class
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
public class Database {
    public static void main(String[] args) {
        String[] friends = {"Friend1", "Friend2"};
        String[] blockedUsers = {"Block1"};
        ArrayList<String> friendsList = new ArrayList<String>(Arrays.asList(friends));
        ArrayList<String> blockedUsersList = new ArrayList<String>(Arrays.asList(blockedUsers));
        User user = new User("Chase Gamble", "Password123!", 19,
                "Chase Gamble - PFP.png", friendsList, blockedUsersList, false);

        DatabaseHelper helper = new DatabaseHelper();
        System.out.printf("DBHelper.writeUser -> %s\n", helper.writeUser(user) ? "SUCCEEDED" : "FAILED");
        System.out.printf("DBHelper.readUser -> \n%s\n", helper.readUser(user.getUsername()).toString());

        BufferedImage image = helper.readImage(user.getUserPFPFile());
        System.out.printf("DBHelper.readImage -> %s\n", image != null ? "SUCCEEDED" : "FAILED");
        System.out.printf("DBHelper.writeImage -> %s\n",
                helper.writeImage(image, user.getUserPFPFile()) ? "SUCCEEDED" : "FAILED");

        MessageHistory history = helper.readMessageHistory("Chase Gamble", "Steven Krauter");
        System.out.println(history.toString());
        history.addMessage("Chase Gamble", "Goodbye...");
        System.out.println(history.toString());
        helper.writeMessageHistory(history);
    }
}
