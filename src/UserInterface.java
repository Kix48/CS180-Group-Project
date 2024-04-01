import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
/**
 * UserInterface.java Interface
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
public interface UserInterface {
    static BufferedImage loadImage(String fileName) {
        return null;
    };

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    int getAge();

    void setAge(int age);

    ArrayList<String> getFriends();

    void addFriends(String username);

    void removeFriends(String username);

    ArrayList<String> getBlockedUsers();

    void addBlockedUsers(String username);

    void removeBlockedUsers(String username);

    boolean isFriendsOnly();

    void setFriendsOnly(boolean friendsOnly);

    String getUserPFPFile();

    void setUserPFPFile(String userPFPFile);

    String toString();
}
