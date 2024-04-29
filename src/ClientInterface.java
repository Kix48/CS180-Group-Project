import java.io.File;
import java.util.ArrayList;

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
    public boolean register(String username, String password, int age, File userPFP) throws Exception;
    public boolean login(String username, String password) throws Exception;
    public User findUser(String username) throws Exception;
    public ArrayList<String> searchUser(String token) throws Exception;
    public boolean addFriend(String friendUsername);
    public boolean removeFriend(String friendUsername);
    public boolean blockUser(String usernameToBlock);
    public boolean unblockUser(String usernameToUnblock);
    public MessageHistory getMessageHistory(String username);
    public ArrayList<MessageHistory> searchMessageHistories(String token) throws Exception;
    public boolean sendMessage(String receiver, String message);
    public boolean removeMessage(String receiver, int messageIdx);
    public boolean setFriendsOnly(boolean friendsOnly) throws Exception;
}
