import java.util.Date;
/**
 * MessageInfoInterface.java Interface
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
public interface MessageInfoInterface {
    public String getSender();
    public String getReceiver();
    public Date getDate();
}
