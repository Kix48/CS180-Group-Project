import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;


public class User {

    private String username;
    private String password;
    private int age;
    private ArrayList<String> friends;
    private ArrayList<String> blockedUsers;
    private boolean friendsOnly;
    private String userPFPFile;


    public User(String username, String password, int age) {

        if (username == null) {
            throw new NullPointerException();
        }

        if (!(age >= 18)) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.password = password;
        this.age = age;
        friends = new ArrayList<>();
        blockedUsers = new ArrayList<>();
        friendsOnly = false;

    } //end constructor

    public User(String username, String password, int age,
                ArrayList<String> friends, ArrayList<String> blockedUsers, boolean friendsOnly) {

        if (username == null) {
            throw new NullPointerException();
        }

        if (!(age >= 18)) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.password = password;
        this.age = age;
        this.friends = friends;
        this.blockedUsers = blockedUsers;
        this.friendsOnly = friendsOnly;

    } //end constructor

    public static BufferedImage loadImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (Exception e) {
            System.err.println("image not found: " + fileName);
            System.exit(-1);
        }
        return image;
    }


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

        if (age >= 18) {
            this.age = age;
        } else {
            System.out.println("Error: Age Must be at least 18");
        }
    } //sets age

    public ArrayList<String> getFriends() { //returns friends ArrayList
        return friends;
    }

    public void addFriends(String username) { //adds a friend, checks if already added, removes from blocks if there
        if (friends.contains(username)) {
            System.out.println("Friend already added!");
        } else if (blockedUsers.contains(username)) {
            removeBlockedUsers(username);
            friends.add(username);
        } else {
            friends.add(username);
        }
    }

    public void removeFriends(String username) { //removes a friend, checks if already in list
        if (friends.contains(username)) {
            friends.remove(username);
        } else {
            System.out.println("User not in friends list!");
        }
    }

    public ArrayList<String> getBlockedUsers() { //returns blockedUser ArrayList
        return blockedUsers;
    }

    public void addBlockedUsers(String username) { //adds blocked user, removes from friends if needed
        if (blockedUsers.contains(username)) {
            System.out.println("Already added to blocked users!");
        } else {
            if (friends.contains(username)) {
                removeFriends(username);
                blockedUsers.add(username);
            } else {
                blockedUsers.add(username);
            }

        }
    }

    public void removeBlockedUsers(String username) { //removes user from blocked
        if (blockedUsers.contains(username)) {
            blockedUsers.remove(username);
        } else {
            System.out.println("User not in blocked list!");
        }
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
        this.userPFPFile = userPFPFile; //TODO: check if valid file
    }

    public String toString() { //returns a toString format

        String output = "";

        output += "Username: " + username + "\n";
        output += "Password: " + password + "\n";
        output += "Age: " + age + "\n";

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
