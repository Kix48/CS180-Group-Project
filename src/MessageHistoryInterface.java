import java.util.Date;
/**
 * MessageHistoryInterface.java Interface
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
public interface MessageHistoryInterface {
    public String getUser1();
    public String getUser2();
    public void addMessage(String sender, String message);
    public void removeMessage(String sender, Date date);
    public String[] getMessages();
}
