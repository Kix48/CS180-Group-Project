import java.awt.image.BufferedImage;

public interface DatabaseHelperInterface {
    public User readUser(String username);
    public boolean writeUser(User user);
    public BufferedImage readImage(String filename);
    public boolean writeImage(BufferedImage image, String filename);
    public MessageHistory readMessageHistory(String username1, String username2);
    public boolean writeMessageHistory(MessageHistory history);
}
