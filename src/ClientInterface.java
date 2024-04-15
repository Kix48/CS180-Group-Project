import java.io.File;
/**
 * ClientInterface.java Interface
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
public interface ClientInterface {
    public boolean initialize();
    public void shutdown();
    public boolean register(String username, String password, int age, File userPFP);
    public boolean login(String username, String password);
    public User findUser(String username);
    public boolean addFriend(String friendUsername);
    public boolean blockUser(String usernameToBlock);
    public MessageHistory getMessageHistory(String username);
    public boolean sendMessage(String receiver, String message);
    public boolean removeMessage(int messageIdx);
    public boolean setFriendsOnly(boolean friendsOnly);
}
