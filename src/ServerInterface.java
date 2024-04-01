public interface ServerInterface {

    boolean createUser(User user);
    boolean modifyUser (User user);
    boolean deleteUser (String username);
    boolean authenticate(String username, String password);
    MessageHistory getMessageHistory(String username1, String username2);
    boolean sendMessage(Message message, String username);
    User findUser(String username);

}
