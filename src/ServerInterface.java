import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * ServerInterface.java Class
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

public interface ServerInterface {

    public void registerUser();
    public void authenticate();
    public void getMessageHistory();
    public void searchMessageHistories();
    public void sendMessage();
    public void removeMessage();
    public void findUser();
    public void searchUser();
    public void addFriend();
    public void removeFriend();
    public void blockUser();
    public void unblockUser();
    public void changeVisibility();
}
