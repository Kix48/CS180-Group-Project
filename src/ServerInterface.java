import java.io.BufferedReader;
import java.io.PrintWriter;

public interface ServerInterface {

    public void registerUser();
    public void authenticate();
    public void getMessageHistory();
    public void sendMessage();
    public void removeMessage();
    public void findUser();
    public void addFriend();
    public void blockUser();
    public void changeVisibility();
}
