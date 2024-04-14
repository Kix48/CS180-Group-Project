import java.io.BufferedReader;
import java.io.PrintWriter;

public interface ServerInterface {

    public void registerUser();
    public void modifyUser();
    public void deleteUser();
    public void authenticate();
    public void getMessageHistory();
    public void sendMessage();
    public void findUser();
    public void addFriend();
    public void blockUser();
}
