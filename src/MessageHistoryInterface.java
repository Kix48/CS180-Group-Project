import java.util.Date;

public interface MessageHistoryInterface {
    public void addMessage(String sender, String receiver, String message);
    public void removeMessage(String sender, Date date);
    public String[] getMessages();
}
