import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
/**
 * User.java Class
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

public class User implements UserInterface {

    private String username;
    private String password;
    private int age;
    private ArrayList<String> friends;
    private ArrayList<String> blockedUsers;
    private boolean friendsOnly;
    private String userPFPFile; // Only used in the server
    private BufferedImage userPFPImage; // Only used in the client

    public User(String username, int age, BufferedImage userPFPImage, boolean isFriendsOnly,
                ArrayList<String> friends, ArrayList<String> blockedUsers) {

        if (username == null) {
            throw new NullPointerException();
        }

        if (!(age > 0)) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.password = "";
        this.age = age;
        this.userPFPFile = "";
        this.userPFPImage = userPFPImage;
        this.friends = friends;
        this.blockedUsers = blockedUsers;
        this.friendsOnly = isFriendsOnly;

    } //end constructor

    public User(String username, String password, int age, String userPFPFile) {

        if (username == null) {
            throw new NullPointerException();
        }

        if (!(age > 0)) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.password = password;
        this.age = age;
        this.userPFPFile = userPFPFile;
        this.userPFPImage = null;
        friends = new ArrayList<>();
        blockedUsers = new ArrayList<>();
        friendsOnly = false;

    } //end constructor

    public User(String username, String password, int age, String userPFPFile,
                ArrayList<String> friends, ArrayList<String> blockedUsers, boolean friendsOnly) {

        if (username == null) {
            throw new NullPointerException();
        }

        if (!(age > 0)) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.password = password;
        this.age = age;
        this.userPFPFile = userPFPFile;
        this.userPFPImage = null;
        this.friends = friends;
        this.blockedUsers = blockedUsers;
        this.friendsOnly = friendsOnly;

    } //end constructor

    public String getUsername() { //return username
        return username;
    }

    public void setUsername(String username) { //set username
        this.username = username;
    }

    public String getPassword() {
        return password;
    } //return password

    public void setPassword(String password) { //set password
        this.password = password;
    }

    public int getAge() { //returns age
        return age;
    } //return age

    public void setAge(int age) {

        if (age > 0) {
            this.age = age;
        } else {
            System.out.println("Error: Age Must be at least 1");
        }
    } //sets age

    public ArrayList<String> getFriends() { //returns friends ArrayList
        return friends;
    }

    public boolean addFriends(String name) { //adds a friend, checks if already added, removes from blocks if there

        if (friends.contains(name)) {
            System.out.println("Friend already added!");
            return false;
        } else if (blockedUsers.contains(name)) {
            removeBlockedUsers(name);
            friends.add(name);
        } else {
            friends.add(name);
        }

        return true;
    }

    public boolean removeFriends(String name) { //removes a friend, checks if already in list
        if (friends.contains(name)) {
            friends.remove(name);
        } else {
            System.out.println("User not in friends list!");
            return false;
        }

        return true;
    }

    public ArrayList<String> getBlockedUsers() { //returns blockedUser ArrayList
        return blockedUsers;
    }

    public boolean addBlockedUsers(String name) { //adds blocked user, removes from friends if needed
        if (blockedUsers.contains(name)) {
            System.out.println("Already added to blocked users!");
            return false;
        } else {
            if (friends.contains(name)) {
                removeFriends(name);
                blockedUsers.add(name);
            } else {
                blockedUsers.add(name);
            }
        }

        return true;
    }

    public boolean removeBlockedUsers(String name) { //removes user from blocked
        if (blockedUsers.contains(name)) {
            blockedUsers.remove(name);
        } else {
            System.out.println("User not in blocked list!");
            return false;
        }

        return true;
    }

    public boolean isFriendsOnly() { //returns if friendsOnly T/F
        return friendsOnly;
    }

    public void setFriendsOnly(boolean friendsOnly) { //sets bFriendsOnly
        this.friendsOnly = friendsOnly;
    }

    public String getUserPFPFile() { //returns userPFP file name
        return userPFPFile;
    }

    public void setUserPFPFile(String userPFPFile) {
        this.userPFPFile = userPFPFile;
    }

    public BufferedImage getUserPFPImage() { //returns userPFP file name
        return userPFPImage;
    }

    public void setUserPFPImage(BufferedImage userPFPImage) {
        this.userPFPImage = userPFPImage;
    }

    public String toString() { //returns a toString format

        String output = "";

        output += "Username: " + username + "\n";
        output += "Password: " + password + "\n";
        output += "Age: " + age + "\n";
        output += "UserPFPFile: " + userPFPFile + "\n";

        if (getFriends().isEmpty()) {
            output += "Friends: None" + "\n";

        } else {
            output += "Friends: " + friends + "\n";
        }

        if (getBlockedUsers().isEmpty()) {
            output += "BlockedUsers: None" + "\n";
        } else {
            output += "BlockedUsers: " + blockedUsers + "\n";
        }

        output += "FriendsOnly: " + this.friendsOnly + "\n";

        return output;
    }

    /*
    toString format:

    Username: David Matthews
    Password: password123
    Age: 21
    Friends: [Mark, John Matthews, NameHere]
    BlockedUsers: None //if 'none' condition is true
     */

} //end class
