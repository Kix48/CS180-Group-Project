import java.io.BufferedReader;
import java.io.PrintWriter;

public interface ServerInterface {

    public void registerUser(BufferedReader reader, PrintWriter writer, DatabaseHelper databaseHelper);
    public boolean modifyUser(User user);
    public boolean deleteUser(String username);
    public boolean authenticate(String username, String password);
    public MessageHistory getMessageHistory(String username1, String username2);
    public boolean sendMessage(Message message, String username);
    public User findUser(String username);
}
