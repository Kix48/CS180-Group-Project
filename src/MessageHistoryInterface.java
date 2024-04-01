import java.util.Date;

public interface MessageHistoryInterface {
    public String getUser1();
    public String getUser2();
    public void addMessage(String sender, String message);
    public void removeMessage(String sender, Date date);
    public String[] getMessages();
}
