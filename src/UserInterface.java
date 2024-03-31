import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

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
