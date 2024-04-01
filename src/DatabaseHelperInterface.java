import java.awt.image.BufferedImage;
/**
 * DatabaseHelperInterface.java Interface
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
public interface DatabaseHelperInterface {
    public User readUser(String username);
    public boolean writeUser(User user);
    public BufferedImage readImage(String filename);
    public boolean writeImage(BufferedImage image, String filename);
    public MessageHistory readMessageHistory(String username1, String username2);
    public boolean writeMessageHistory(MessageHistory history);
}
